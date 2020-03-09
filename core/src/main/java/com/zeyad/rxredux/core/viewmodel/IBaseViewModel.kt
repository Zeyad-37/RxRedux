package com.zeyad.rxredux.core.viewmodel

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jakewharton.rx.ReplayingShare
import com.zeyad.rxredux.core.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

@Throws(IllegalStateException::class)
inline fun <reified T> T.throwIllegalStateException(result: Any): Nothing =
        throw IllegalStateException("Can not reduce from $this to ${T::class.java.simpleName} with $result")

interface IBaseViewModel<I, R, S : Parcelable, E> {

    var currentPModel: S

    var disposable: Disposable

    val intents: PublishSubject<I>

    fun stateReducer(newResult: R, currentState: S): S

    fun reduceIntentsToResults(intent: I, currentState: S): Flowable<*>

    fun errorMessageFactory(throwable: Throwable, intent: I?): String = throwable.message.orEmpty()

    fun middleware(it: PModel<*, I>) {
        if (it is ErrorEffect) Log.e(this.javaClass.simpleName, "Error", it.error)
        else Log.d(this.javaClass.simpleName, "PModel: $it")
    }

    fun offer(intent: I): Unit = intents.onNext(intent)

    fun store(initialState: S, intentStream: Observable<I>): LiveData<PModel<*, I>> {
        currentPModel = initialState
        val pModels = intentStream.mergeWith(intents).toResult()
        val states = stateStream(pModels as Flowable<Result<R, I>>, initialState)
        val effects = effectStream(pModels as Flowable<Result<E, I>>)
        val liveState = MutableLiveData<PModel<*, I>>()
        disposable = Flowable.merge(states, effects)
                .doAfterNext { middleware(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ pModel: PModel<*, I> -> liveState.value = pModel }, { throw it })
        return liveState
    }

    private fun Observable<I>.toResult(): Flowable<Result<*, I>> {
        return observeOn(Schedulers.computation())
                .toFlowable(BackpressureStrategy.BUFFER)
                .concatMap { intent: I ->
                    reduceIntentsToResults(intent, currentPModel)
                            .map<Result<*, I>> {
                                if (it is EffectResult<*, *>) it as EffectResult<*, I>
                                else SuccessResult(it, intent)
                            }.onErrorReturn { ErrorEffectResult(it, intent) }
                            .startWith(LoadingEffectResult(intent))
                }.share()
    }

    private fun stateStream(results: Flowable<Result<R, I>>, initialState: S): Flowable<PState<S, I>> {
        return results.filter { it is SuccessResult }
                .map { it as SuccessResult }
                .scan(SuccessState(initialState, null) as PState<S, I>, stateReducer())
                .map {
                    if (currentPModel == it.bundle && initialState != it.bundle) {
                        EmptySuccessState as PState<S, I>
                    } else {
                        currentPModel = it.bundle
                        it
                    }
                }.compose(ReplayingShare.instance())
    }

    private fun effectStream(results: Flowable<Result<E, I>>): Flowable<PEffect<E?, I>> {
        return results.filter { it is EffectResult }
                .map { it as EffectResult }
                .scan(InitialSuccessEffect as PEffect<E?, I>, effectReducer())
                .distinctUntilChanged()
    }

    private fun stateReducer(): BiFunction<PState<S, I>, SuccessResult<R, I>, PState<S, I>> =
            BiFunction { currentPModel: PState<S, I>, result: SuccessResult<R, I> ->
                SuccessState(stateReducer(result.bundle, currentPModel.bundle), result.intent)
            }

    private fun effectReducer(): BiFunction<PEffect<E?, I>, in EffectResult<E, I>, PEffect<E?, I>> =
            BiFunction { currentPEffect, result ->
                when (result) {
                    is LoadingEffectResult -> LoadingEffect(currentPEffect.bundle, result.intent)
                    is SuccessEffectResult -> result.successEffect(currentPEffect)
                    is ErrorEffectResult -> result.errorEffect(currentPEffect)
                    is EmptyEffectResult -> EmptySuccessEffect(result.intent, currentPEffect.bundle)
                }
            }

    private fun SuccessEffectResult<E, I>.successEffect(currentPEffect: PEffect<E?, I>): SuccessEffect<E?, I> =
            when (currentPEffect) {
                is LoadingEffect -> SuccessEffect(bundle, intent)
                is InitialSuccessEffect, is EmptySuccessEffect, is SuccessEffect, is ErrorEffect ->
                    currentPEffect.throwIllegalStateException(this)
            }

    private fun ErrorEffectResult<I>.errorEffect(currentPEffect: PEffect<E?, I>): ErrorEffect<E?, I> =
            when (currentPEffect) {
                is LoadingEffect ->
                    ErrorEffect(error, errorMessageFactory(error, intent), currentPEffect.bundle, intent)
                is InitialSuccessEffect, is EmptySuccessEffect, is SuccessEffect, is ErrorEffect ->
                    currentPEffect.throwIllegalStateException(this)
            }

    fun onClearImpl() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}
