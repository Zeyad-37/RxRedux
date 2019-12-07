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

    var currentPModel: Any

    var disposable: Disposable

    val intents: PublishSubject<I>

    fun stateReducer(newResult: R, currentState: S): S

    fun reduceIntentsToResults(intent: I, currentState: Any): Flowable<*>

    fun errorMessageFactory(throwable: Throwable, intent: I, currentStateBundle: E): String =
            throwable.message.orEmpty()

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
        Flowable.merge(states, effects)
                .doAfterNext { middleware(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { pModel: PModel<*, I> -> liveState.value = pModel }
                .also { disposable = it }
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
                }
                .share()
    }

    private fun stateStream(pModels: Flowable<Result<R, I>>, initialState: S): Flowable<PModel<S, I>> {
        return pModels.filter { it is SuccessResult }
                .map { it as SuccessResult }
                .scan(SuccessState(initialState, null) as PModel<S, I>, stateReducer())
                .map {
                    if (currentPModel == it.bundle && initialState != it.bundle) {
                        EmptySuccessState() as PModel<S, I>
                    } else {
                        currentPModel = it.bundle
                        it
                    }
                }
                .compose(ReplayingShare.instance())
    }

    private fun effectStream(pModels: Flowable<Result<E, I>>): Flowable<PEffect<E, I>> {
        return pModels.filter { it is EffectResult }
                .map { it as EffectResult }
                .scan(EmptySuccessEffect() as PEffect<E, I>, effectReducer())
                .filter { t: PEffect<E, I> -> t !is EmptySuccessEffect }
                .distinctUntilChanged()
                .doOnNext { pEffect: PEffect<E, I> -> (pEffect as? SuccessEffectResult<*, *>)?.also { currentPModel = it } }
    }

    private fun stateReducer(): BiFunction<PModel<S, I>, SuccessResult<R, I>, PModel<S, I>> =
            BiFunction { currentUIModel: PModel<S, I>, result: SuccessResult<R, I> ->
                SuccessState(stateReducer(result.bundle, currentUIModel.bundle), result.intent)
            }

    private fun effectReducer(): BiFunction<PEffect<E, I>, in EffectResult<E, I>, PEffect<E, I>> =
            BiFunction { currentUIModel: PEffect<E, I>, result: EffectResult<E, I> ->
                when (result) {
                    is LoadingEffectResult -> LoadingEffect(currentUIModel.bundle, result.intent)
                    is SuccessEffectResult -> result.successEffect(currentUIModel)
                    is ErrorEffectResult -> result.errorEffect(currentUIModel)
                }
            }

    private fun SuccessEffectResult<E, I>.successEffect(currentUIModel: PEffect<E, I>): SuccessEffect<E, I> =
            when (currentUIModel) {
                is LoadingEffect -> SuccessEffect(bundle, intent)
                is EmptySuccessEffect, is SuccessEffect, is ErrorEffect -> currentUIModel.throwIllegalStateException(this)
            }

    private fun ErrorEffectResult<I>.errorEffect(currentUIModel: PEffect<E, I>): ErrorEffect<E, I> =
            when (currentUIModel) {
                is LoadingEffect -> ErrorEffect(error, errorMessageFactory(error, intent, currentUIModel.bundle),
                        currentUIModel.bundle, intent)
                is EmptySuccessEffect, is SuccessEffect, is ErrorEffect -> currentUIModel.throwIllegalStateException(this)
            }

    fun onClearImpl() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }
}
