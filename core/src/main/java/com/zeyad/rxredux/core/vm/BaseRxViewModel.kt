package com.zeyad.rxredux.core.vm

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.zeyad.rxredux.core.vm.rxvm.Effect
import com.zeyad.rxredux.core.vm.rxvm.Error
import com.zeyad.rxredux.core.vm.rxvm.Input
import com.zeyad.rxredux.core.vm.rxvm.LoggingListener
import com.zeyad.rxredux.core.vm.rxvm.LoggingListenerHelper
import com.zeyad.rxredux.core.vm.rxvm.Progress
import com.zeyad.rxredux.core.vm.rxvm.Result
import com.zeyad.rxredux.core.vm.rxvm.State
import com.zeyad.rxredux.core.vm.rxvm.TrackingListener
import com.zeyad.rxredux.core.vm.rxvm.TrackingListenerHelper
import com.zeyad.rxredux.core.vm.rxvm.ViewModelListener
import com.zeyad.rxredux.core.vm.rxvm.ViewModelListenerHelper
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.Disposables
import io.reactivex.subjects.PublishSubject
import org.reactivestreams.Subscriber
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

typealias Tracker = TrackingListenerHelper.() -> Unit
typealias Logger = LoggingListenerHelper.() -> Unit

private const val ARG_STATE = "arg_state"
private const val DEBOUNCE_INTERVAL: Long = 200L
private const val THROTTLE_INTERVAL: Long = 500L

abstract class BaseRxViewModel<I : Input, S : State, R : Result, E : Effect> : ViewModel(), ModelViewIntent<I, S> {

    open class RxOutcome(open var input: Input = EmptyInput)

    private object EmptyInput : Input()

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    data class RxState(val state: State, override var input: Input = EmptyInput) : RxOutcome()

    internal data class RxEffect(val effect: Effect) : RxOutcome()
    internal data class RxResult(val result: Result) : RxOutcome()
    internal data class RxProgress(val progress: Progress) : RxOutcome()
    internal object RxEmpty : RxOutcome()
    internal data class RxError(var error: Error) : RxOutcome() {
        override var input: Input = EmptyInput
            set(value) {
                error = error.copy(input = value)
                field = value
            }
    }

    var savedStateHandle: SavedStateHandle? = null
    internal var disposable = Disposables.empty()

    private var viewModelListener: ViewModelListener? = null
        set(value) {
            field = value
            viewModelListener?.states?.invoke(currentState)
        }

    private var trackingListener: TrackingListener = initTracking()
    private var loggingListener: LoggingListener = initLogging()

    protected lateinit var currentState: S

    private val offeredInputs = PublishSubject.create<I>()
    private val throttledInputs = PublishSubject.create<I>()
    private val debouncedInputs = PublishSubject.create<I>()

    internal var progress by Delegates.observable(Progress(false, EmptyInput), { _, oldValue, newValue ->
        if (newValue != oldValue) {
            notifyProgressChanged(newValue)
        }
    })

    override fun offer(input: I, inputStrategy: InputStrategy) =
            resolveInputStream(inputStrategy).onNext(input)

    private fun resolveInputStream(inputStrategy: InputStrategy) = when (inputStrategy) {
        InputStrategy.NONE -> offeredInputs
        InputStrategy.THROTTLE -> throttledInputs
        InputStrategy.DEBOUNCE -> debouncedInputs
    }

    /**
     * Input source provider. By default it returns empty
     * It can be overwritten to provide other inputs into the stream
     */
    open fun inputsSource(): Observable<I> = Observable.empty()

    internal fun trackEvents(event: RxOutcome) {
        when (event) {
            is RxProgress -> trackingListener.progress(event.progress)
            is RxEffect -> trackingListener.effects(event.effect, event.input)
            is RxError -> trackingListener.errors(event.error)
            is RxResult -> trackingListener.results(event.result, event.input)
        }
    }

    internal fun logEvents(event: RxOutcome) {
        when (event) {
            is RxProgress -> loggingListener.progress(event.progress)
            is RxEffect -> loggingListener.effects(event.effect)
            is RxError -> loggingListener.errors(event.error)
            is RxResult -> loggingListener.results(event.result)
        }
    }

    private fun trackInput(input: Input) {
        trackingListener.inputs(input)
    }

    private fun logInput(input: Input) {
        loggingListener.inputs(input)
    }

    internal fun trackState(state: State, input: Input) {
        trackingListener.states(state, input)
    }

    internal fun logState(state: State) {
        loggingListener.states(state)
    }

    private fun initTracking(): TrackingListener {
        val trackingListenerHelper = TrackingListenerHelper()
        val init = track()
        trackingListenerHelper.init()
        return trackingListenerHelper
    }

    private fun initLogging(): LoggingListener {
        val loggingListenerHelper = LoggingListenerHelper()
        val init = log()
        loggingListenerHelper.init()
        return loggingListenerHelper
    }

    private fun processInputOutcomeStream(inputOutcomeStream: InputOutcomeStream<I>): Flowable<RxOutcome> {
        val result = inputOutcomeStream.outcomes
                .map { rxOutcome -> addInput(rxOutcome, inputOutcomeStream.input) }
                .onErrorReturn { throwable -> createRxError(throwable, inputOutcomeStream.input) }
        return if (inputOutcomeStream.input.showProgress.not()) {
            result
        } else {
            result.startWith(RxProgress(Progress(isLoading = true, input = inputOutcomeStream.input)))
        }
    }

    private fun addInput(outcome: RxOutcome, input: I): RxOutcome {
        return outcome.apply { outcome.input = input }
    }

    private fun createRxError(throwable: Throwable, input: I): RxError {
        return RxError(Error(throwable.message.orEmpty(), throwable, input))
                .apply { this.input = input }
    }

    internal fun handleResult(result: RxOutcome) {
        if (result is RxProgress) {
            notifyProgressChanged(result.progress)
        } else {
            dismissProgressDependingOnInput(result.input)
        }

        when (result) {
            is RxError -> notifyError(result.error)
            is RxEffect -> notifyEffect(result.effect)
            is RxState -> {
                saveState(result.state)
                notifyNewState(result.state)
            }
        }
    }

    protected open fun log(): Logger = { /*empty*/ }

    protected open fun track(): Tracker = { /*empty*/ }

    private fun dismissProgressDependingOnInput(input: Input?) {
        if (input?.showProgress == true) {
            notifyProgressChanged(Progress(false, input))
        }
    }

    override fun observe(lifecycleOwner: LifecycleOwner, init: ViewModelListenerHelper.() -> Unit) {
        val helper = ViewModelListenerHelper()
        helper.init()
        this.viewModelListener = helper
        removeObservers(lifecycleOwner)
    }

    override fun observe(lifecycleOwner: LifecycleOwner, viewModelListener: ViewModelListener) {
        this.viewModelListener = viewModelListener
        removeObservers(lifecycleOwner)
    }

    private fun notifyProgressChanged(progress: Progress) {
        viewModelListener?.progress?.invoke(progress)
    }

    private fun notifyEffect(effect: Effect) {
        viewModelListener?.effects?.invoke(effect)
    }

    private fun notifyError(error: Error) {
        viewModelListener?.errors?.invoke(error)
    }

    private fun notifyNewState(state: State) {
        currentState = state as S
        viewModelListener?.states?.invoke(state)
    }

    private fun saveState(state: State) {
        savedStateHandle?.set(ARG_STATE, state)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    private fun removeObservers(lifecycleOwner: LifecycleOwner) {
        val lifecycleObserver = object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                viewModelListener = null
                disposable.dispose()
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    private fun Observable<I>.configureStream(): Flowable<InputOutcomeStream<I>> {
        return this.mergeWith(inputsSource())
                .mergeWith(offeredInputs)
                .mergeWith(throttledInputs.throttleFirst(THROTTLE_INTERVAL, TimeUnit.MILLISECONDS))
                .mergeWith(debouncedInputs.debounce(DEBOUNCE_INTERVAL, TimeUnit.MILLISECONDS))
                .toFlowable(BackpressureStrategy.BUFFER)
                .doOnNext {
                    trackInput(it)
                    logInput(it)
                }.map { InputOutcomeStream(it, handleInputs(it)) }
    }

    private fun createOutcomes(inputs: () -> Observable<I>): Flowable<RxOutcome> {
        val streamsToProcess = inputs().configureStream().share()

        val asyncOutcomes = streamsToProcess.filter { it.outcomes is AsyncOutcomeFlowable }
                .flatMap(::processInputOutcomeStream)

        val sequentialOutcomes = streamsToProcess.filter { it.outcomes !is AsyncOutcomeFlowable }
                .concatMap(::processInputOutcomeStream)

        return Flowable.merge(asyncOutcomes, sequentialOutcomes).share()
    }

    override fun bind(initialState: S, inputs: () -> Observable<I>): ModelViewIntent<I, S> {
        currentState = savedStateHandle?.get(ARG_STATE) ?: initialState
        bindInputs(inputs)
        return this
    }

    private fun bindInputs(inputs: () -> Observable<I>) {
        processOutcomes(createOutcomes(inputs))
    }

    abstract fun processOutcomes(outcomes: Flowable<RxOutcome>)

    protected fun Flowable<out E>.asyncOutcome(): AsyncOutcomeFlowable {
        return AsyncOutcomeFlowable(map { RxEffect(it) })
    }

    protected class AsyncOutcomeFlowable(private val flowable: Flowable<out RxOutcome>) : Flowable<RxOutcome>() {
        override fun subscribeActual(subscriber: Subscriber<in RxOutcome>?) {
            flowable.subscribe(subscriber)
        }
    }

    internal fun Input.effectStream(effect: (input: Input) -> Flowable<Effect>): Flowable<Effect> =
            effect(this)

    internal fun <T> Input.actionStream(state: (input: Input) -> Flowable<T>): Flowable<T> where T : Result =
            state(this)

    internal data class InputOutcomeStream<I>(val input: I, val outcomes: Flowable<out RxOutcome>)
}
