package com.zeyad.rxredux.core.viewmodel.coroutines

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.zeyad.rxredux.core.*
import com.zeyad.rxredux.core.viewmodel.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.*
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
abstract class Machine<I, R, S : Parcelable, E> : ViewModel(), CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    private val _events = Channel<I>(Channel.CONFLATED)
    val events: SendChannel<I> get() = _events

    private val updateFlows = Channel<Flow<Result<*, I>>>(Channel.UNLIMITED)
    private val emission = Channel<Any>(Channel.UNLIMITED)
    val pModels = Channel<PModel<*, I>>()

    @UseExperimental(FlowPreview::class)
    fun store(initialState: S) {
        launch(Dispatchers.Default) {
            _events.consumeEach { event ->
                updateFlows.send(reduceEventsToResults(event, emission.receive())
                        .map {
                            if (it is EffectResult<*, *>) it as EffectResult<*, I>
                            else SuccessResult(it, event)
                        }
                        .onStart { emit(LoadingEffectResult(event)) }
                        .catch { emit(ErrorEffectResult(it, event)) }
                        .distinctUntilChanged())

                updateFlows.consumeEach { updates ->
                    flowOf(updates.filter { it is SuccessResult<*, *> }
                            .map { it as SuccessResult<R, I> }
                            .scan(SuccessState(initialState, null) as PModel<S, I>)
                            { pModel, result ->
                                SuccessState(stateReducer(result.bundle, pModel.bundle), result.event)
                            }
                            .map {
                                if (emission.receive() == it.bundle && initialState != it.bundle) {
                                    EmptySuccessState() as PModel<S, I>
                                } else {
                                    emission.send(it.bundle)
                                    it
                                }
                            }, updates.filter { it is EffectResult<*, *> }
                            .map { it as EffectResult<E, I> }
                            .scan(EmptySuccessEffect() as PEffect<E, I>)
                            { currentUIModel, result ->
                                result.run {
                                    when (this) {
                                        is LoadingEffectResult -> LoadingEffect(currentUIModel.bundle, event)
                                        is SuccessEffectResult -> successEffect(currentUIModel)
                                        is ErrorEffectResult -> errorEffect(currentUIModel)
                                    }
                                }
                            }
                            .filter { t: PEffect<E, I> -> t !is EmptySuccessEffect }
                            .distinctUntilChanged()
                            .onEach {
                                if (it is SuccessEffectResult<*, *>) {
                                    emission.send(it.bundle!!)
                                }
                            })
                            .flattenMerge()
                            .collect { pModels.send(it) }
                }
            }
        }
    }

    protected abstract fun reduceEventsToResults(event: I, current: Any): Flow<*>

    protected abstract fun stateReducer(update: R, current: S): S

    private fun SuccessEffectResult<E, I>.successEffect(currentUIModel: PEffect<E, I>): SuccessEffect<E, I> =
            when (currentUIModel) {
                is LoadingEffect -> SuccessEffect(bundle, event)
                is EmptySuccessEffect, is SuccessEffect, is ErrorEffect -> currentUIModel.throwIllegalStateException(this)
            }

    private fun ErrorEffectResult<I>.errorEffect(currentUIModel: PEffect<E, I>): ErrorEffect<E, I> =
            when (currentUIModel) {
                is LoadingEffect -> ErrorEffect(error, errorMessageFactory(error, event, currentUIModel.bundle),
                        currentUIModel.bundle, event)
                is EmptySuccessEffect, is SuccessEffect, is ErrorEffect -> currentUIModel.throwIllegalStateException(this)
            }

    fun errorMessageFactory(throwable: Throwable, event: I, currentStateBundle: E): Message =
            StringMessage(throwable.localizedMessage)

    override fun onCleared() {
        coroutineContext.cancel()
        super.onCleared()
    }
}