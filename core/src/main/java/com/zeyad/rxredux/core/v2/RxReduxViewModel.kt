package com.zeyad.rxredux.core.v2

import android.util.Log
import androidx.lifecycle.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import org.reactivestreams.Subscriber
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates.observable

const val ARG_STATE = "arg_state"

class AsyncOutcomeFlowable(val flowable: Flowable<RxOutcome>) : Flowable<RxOutcome>() {
    override fun subscribeActual(s: Subscriber<in RxOutcome>?) = Unit
}

data class InputOutcomeStream(val input: Input, val outcomes: Flowable<RxOutcome>)

internal object EmptyInput : Input()

open class RxOutcome(open var input: Input = EmptyInput)

object EmptyOutcome : RxOutcome()

private data class RxProgress(val progress: Progress) : RxOutcome()

internal data class RxError(var error: Error) : RxOutcome() {
    override var input: Input = EmptyInput
        set(value) {
            error = error.copy(input = value)
            field = value
        }
}

abstract class RxReduxViewModel<I : Input, R : Result, S : State, E : Effect>(
        private val reducer: Reducer<S, R>,
        private val inputHandler: InputHandler<I, S>,
        private val savedStateHandle: SavedStateHandle?,
) : ViewModel() {

    private data class RxState<S>(val state: S) : RxOutcome()
    internal data class RxEffect<E>(val effect: E) : RxOutcome()
    internal data class RxResult<R>(val result: R) : RxOutcome()

    private lateinit var disposable: Disposable
    private lateinit var currentState: S
    private var viewModelListener: ViewModelListener<S, E>? = null
        set(value) {
            value?.states?.invoke(currentState)
            field = value
        }
    private var progress: Progress by observable(Progress(false, EmptyInput),
            { _, oldValue, newValue ->
                if (newValue != oldValue) notifyProgressChanged(newValue)
            })

    private val inputs: PublishSubject<I> = PublishSubject.create()
    private val throttledInputs: PublishSubject<I> = PublishSubject.create()
    private val debouncedInputs: PublishSubject<I> = PublishSubject.create()
    private val trackingListener: TrackingListener<I, R, S, E> = this.initTracking()
    private val loggingListener: LoggingListener<I, R, S, E> = this.initLogging()

    fun bind(initialState: S, inputs: () -> Observable<I>): RxReduxViewModel<I, R, S, E> {
        currentState = savedStateHandle?.get(ARG_STATE) ?: initialState
        bindInputs(inputs)
        return this
    }

    fun observe(lifecycleOwner: LifecycleOwner, init: ViewModelListenerHelper<S, E>.() -> Unit) {
        val helper = ViewModelListenerHelper<S, E>()
        helper.init()
        viewModelListener = helper
        removeObservers(lifecycleOwner)
    }

    /**
     * Input source provider. By default it returns empty
     * It can be overwritten to provide other inputs into the stream
     */
    fun inputSource(): Observable<I> = Observable.empty()

    fun process(input: I, inputStrategy: InputStrategy = InputStrategy.NONE) = when (inputStrategy) {
        InputStrategy.NONE -> inputs
        InputStrategy.THROTTLE -> throttledInputs
        InputStrategy.DEBOUNCE -> debouncedInputs
    }.onNext(input)

    fun log(): LoggingListenerHelper<I, R, S, E>.() -> Unit = {
        inputs { Log.d(this@RxReduxViewModel::class.simpleName, " - Input: $it") }

        progress { Log.d(this@RxReduxViewModel::class.simpleName, " - Progress: $it") }

        results { Log.d(this@RxReduxViewModel::class.simpleName, " - Result: $it") }

        effects { Log.d(this@RxReduxViewModel::class.simpleName, " - Effect: $it") }

        states { Log.d(this@RxReduxViewModel::class.simpleName, " - State: $it") }
    }

    fun track(): TrackingListenerHelper<I, R, S, E>.() -> Unit = { /*empty*/ }

    private fun bindInputs(inputs: () -> Observable<I>) {
        val outcome = createOutcomes(inputs)
        val stateResult = outcome.filter { it is RxResult<*> }.map { it as RxResult<R> }
                .scan(RxState(currentState)) { state: RxState<S>, result: RxResult<R> ->
                    RxState(reducer.reduce(state.state, result.result)).apply { input = result.input }
                }.doOnNext {
                    trackState(it.state, it.input as I)
                    logState(it.state)
                }
        disposable = Flowable.merge(outcome.filter { it !is RxResult<*> }, stateResult)
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    trackEvents(it)
                    logEvents(it)
                    handleResult(it)
                }.subscribe()
    }

    private fun createOutcomes(inputs: () -> Observable<I>): Flowable<RxOutcome> {
        val streamsToProcess = Observable.merge(
                inputs(), inputSource(), this.inputs, //throttledInputs.throttle(InputStrategy.THROTTLE.interval),
                debouncedInputs.debounce(InputStrategy.DEBOUNCE.interval, TimeUnit.MILLISECONDS)
        ).observeOn(Schedulers.computation())
                .toFlowable(BackpressureStrategy.BUFFER)
                .doOnNext {
                    trackInput(it)
                    logInput(it)
                }.map { InputOutcomeStream(it, inputHandler.handleInputs(it, currentState)) }
                .share()
        val asyncOutcomes = streamsToProcess.filter { it.outcomes is AsyncOutcomeFlowable }
                .map { it.copy(outcomes = (it.outcomes as AsyncOutcomeFlowable).flowable) }
                .flatMap { processInputOutcomeStream(it) }
        val sequentialOutcomes = streamsToProcess.filter { it.outcomes !is AsyncOutcomeFlowable }
                .concatMap { processInputOutcomeStream(it) }
        return Flowable.merge(asyncOutcomes, sequentialOutcomes)
    }

    private fun processInputOutcomeStream(inputOutcomeStream: InputOutcomeStream): Flowable<RxOutcome> {
        val result = inputOutcomeStream.outcomes
                .map { it.apply { input = inputOutcomeStream.input } }
                .onErrorReturn { createRxError(it, inputOutcomeStream.input as I) }
        return if (inputOutcomeStream.input.showProgress.not()) {
            result
        } else {
            result.startWith(RxProgress(Progress(isLoading = true, input = inputOutcomeStream.input)))
        }
    }

    private fun trackEvents(event: RxOutcome) {
        when (event) {
            is RxProgress -> trackingListener.progress(event.progress)
            is RxEffect<*> -> trackingListener.effects(event.effect as E, event.input as I)
            is RxError -> trackingListener.errors(event.error)
            is RxResult<*> -> trackingListener.results(event.result as R, event.input as I)
        }
    }

    private fun logEvents(event: RxOutcome) {
        when (event) {
            is RxProgress -> loggingListener.progress(event.progress)
            is RxEffect<*> -> loggingListener.effects(event.effect as E)
            is RxError -> loggingListener.errors(event.error)
            is RxResult<*> -> loggingListener.results(event.result as R)
        }
    }

    private fun trackInput(input: I) = trackingListener.inputs(input)

    private fun logInput(input: I) = loggingListener.inputs(input)

    private fun trackState(state: S, input: I) = trackingListener.states(state, input)

    private fun logState(state: S) = loggingListener.states(state)

    private fun createRxError(throwable: Throwable, input: I): RxError =
            RxError(Error(throwable.message.orEmpty(), throwable, input)).apply { this.input = input }

    private fun handleResult(result: RxOutcome) {
        if (result is RxProgress) {
            notifyProgressChanged(result.progress)
        } else {
            dismissProgressDependingOnInput(result.input as I)
        }
        when (result) {
            is RxError -> notifyError(result.error)
            is RxEffect<*> -> notifyEffect(result.effect as E)
            is RxState<*> -> {
                saveState(result.state as S)
                notifyNewState(result.state)
            }
        }
    }

    private fun dismissProgressDependingOnInput(input: I?) {
        if (input?.showProgress == true) {
            notifyProgressChanged(Progress(false, input))
        }
    }

    private fun notifyProgressChanged(progress: Progress) = viewModelListener?.progress?.invoke(progress)

    private fun notifyEffect(effect: E) = viewModelListener?.effects?.invoke(effect)

    private fun notifyError(error: Error) = viewModelListener?.errors?.invoke(error)

    private fun notifyNewState(state: S) {
        currentState = state
        viewModelListener?.states?.invoke(state)
    }

    private fun initTracking(): TrackingListener<I, R, S, E> {
        val trackingListenerHelper = TrackingListenerHelper<I, R, S, E>()
        val init: TrackingListenerHelper<I, R, S, E>.() -> Unit = track()
        trackingListenerHelper.init()
        return trackingListenerHelper
    }

    private fun initLogging(): LoggingListener<I, R, S, E> {
        val loggingListenerHelper = LoggingListenerHelper<I, R, S, E>()
        val init: LoggingListenerHelper<I, R, S, E>.() -> Unit = log()
        loggingListenerHelper.init()
        return loggingListenerHelper
    }

    private fun saveState(state: S) = savedStateHandle?.set(ARG_STATE, state) ?: Unit

    private fun removeObservers(lifecycleOwner: LifecycleOwner) =
            lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    unBind()
                    lifecycleOwner.lifecycle.removeObserver(this)
                }
            })

    override fun onCleared() = unBind()

    private fun unBind() {
        viewModelListener = null
        disposable.dispose()
    }
}
