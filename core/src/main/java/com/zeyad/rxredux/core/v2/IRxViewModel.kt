package com.zeyad.rxredux.core.v2

import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

const val ARG_STATE = "arg_state"

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

interface IRxViewModel<I : Input, R : Result, S : State, E : Effect> {

    private data class RxState<S>(val state: S) : RxOutcome()
    data class RxEffect<E>(val effect: E) : RxOutcome()
    data class RxResult<R>(val result: R) : RxOutcome()

    var disposable: Disposable
    var currentState: S
    var viewModelListener: ViewModelListener<S, E>?
    var progress: Progress

    val trackingListener: TrackingListener<I, R, S, E>
    val loggingListener: LoggingListener<I, R, S, E>
    val inputs: PublishSubject<I>
    val throttledInputs: PublishSubject<I>
    val debouncedInputs: PublishSubject<I>
    val inputHandler: InputHandler<I, S>
    val reducer: Reducer<S, R>

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

    fun bind(initialState: S, inputs: () -> Observable<I> = { Observable.empty() }): IRxViewModel<I, R, S, E>

    fun saveState(state: S)

    fun log(): LoggingListenerHelper<I, R, S, E>.() -> Unit = {
        inputs { Log.d(this@IRxViewModel::class.simpleName, " - Input: $it") }

        progress { Log.d(this@IRxViewModel::class.simpleName, " - Progress: $it") }

        results { Log.d(this@IRxViewModel::class.simpleName, " - Result: $it") }

        effects { Log.d(this@IRxViewModel::class.simpleName, " - Effect: $it") }

        states { Log.d(this@IRxViewModel::class.simpleName, " - State: $it") }
    }

    fun track(): TrackingListenerHelper<I, R, S, E>.() -> Unit = { /*empty*/ }

    fun bindInputs(inputs: () -> Observable<I>) {
        val outcome = createOutcomes(inputs)
        val stateResult = outcome.filter { it is RxResult<*> }.map { it as RxResult<R> }
                .scan(RxState(currentState)) { state: RxState<S>, result: RxResult<R> ->
                    RxState(reducer.reduce(state.state, result.result)).apply { input = result.input }
                }.doOnNext {
                    trackState(it.state, it.input as I)
                    logState(it.state)
                }
        disposable = Flowable.merge(outcome.filter { it !is RxResult<*> }, stateResult)
                .doOnNext {
                    trackEvents(it)
                    logEvents(it)
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribe { handleResult(it) }
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

    fun notifyProgressChanged(progress: Progress) = viewModelListener?.progress?.invoke(progress)

    private fun notifyEffect(effect: E) = viewModelListener?.effects?.invoke(effect)

    private fun notifyError(error: Error) = viewModelListener?.errors?.invoke(error)

    private fun notifyNewState(state: S) {
        currentState = state
        viewModelListener?.states?.invoke(state)
    }

    fun initTracking(): TrackingListener<I, R, S, E> {
        val trackingListenerHelper = TrackingListenerHelper<I, R, S, E>()
        val init: TrackingListenerHelper<I, R, S, E>.() -> Unit = track()
        trackingListenerHelper.init()
        return trackingListenerHelper
    }

    fun initLogging(): LoggingListener<I, R, S, E> {
        val loggingListenerHelper = LoggingListenerHelper<I, R, S, E>()
        val init: LoggingListenerHelper<I, R, S, E>.() -> Unit = log()
        loggingListenerHelper.init()
        return loggingListenerHelper
    }
}
