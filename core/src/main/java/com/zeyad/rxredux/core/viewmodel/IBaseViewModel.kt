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
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject

@Throws(IllegalStateException::class)
inline fun <reified T> T.throwIllegalStateException(result: Any): Nothing =
        throw IllegalStateException("Can not reduce from $this to ${T::class.java.simpleName} with $result")

interface IBaseViewModel<I, R, S : Parcelable, E> {

    var disposable: CompositeDisposable

    val currentStateStream: BehaviorSubject<Any>

    fun stateReducer(newResult: R, currentState: S): S

    fun reduceEventsToResults(event: I, currentState: Any): Flowable<*>

    fun errorMessageFactory(throwable: Throwable, event: I, currentStateBundle: E): Message =
            StringMessage(throwable.message.orEmpty())

    fun middleware(it: PModel<*, I>) {
        if (it is ErrorEffect) Log.e("IBaseViewModel", "Error", it.error)
        else Log.d("IBaseViewModel", "PModel: $it")
    }

    fun store(events: Observable<I>, initialState: S): LiveData<PModel<*, I>> {
        currentStateStream.onNext(initialState)
        val pModels = events.toResult()
        val states = stateStream(pModels as Flowable<Result<R, I>>, initialState)
        val effects = effectStream(pModels as Flowable<Result<E, I>>)
        val liveState = MutableLiveData<PModel<*, I>>()
        Flowable.merge(states, effects)
                .doAfterNext { middleware(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { t: PModel<*, I> -> liveState.value = t }
                .let { disposable.add(it) }
        return liveState
    }

    private fun stateStream(pModels: Flowable<Result<R, I>>, initialState: S): Flowable<PModel<S, I>> =
            pModels.filter { it is SuccessResult }
                    .map { it as SuccessResult }
                    .scan(SuccessState(initialState, null) as PModel<S, I>, stateReducer())
                    .map {
                        if (currentStateStream.value == it.bundle && initialState != it.bundle) {
                            EmptySuccessState() as PModel<S, I>
                        } else {
                            currentStateStream.onNext(it.bundle)
                            it
                        }
                    }
                    .compose(ReplayingShare.instance())

    private fun effectStream(pModels: Flowable<Result<E, I>>): Flowable<PEffect<E, I>> =
            pModels.filter { it is EffectResult }
                    .map { it as EffectResult }
                    .scan(EmptySuccessEffect() as PEffect<E, I>, effectReducer())
                    .filter { t: PEffect<E, I> -> t !is EmptySuccessEffect }
                    .distinctUntilChanged()
                    .doOnNext {
                        if (it is SuccessEffectResult<*, *>) {
                            currentStateStream.onNext(it.bundle!!)
                        }
                    }

    private fun Observable<I>.toResult(): Flowable<Result<*, I>> =
            toFlowable(BackpressureStrategy.BUFFER)
                    .observeOn(Schedulers.computation())
                    .concatMap { event ->
                        reduceEventsToResults(event, currentStateStream.value!!)
                                .map<Result<*, I>> {
                                    if (it is EffectResult<*, *>) it as EffectResult<*, I>
                                    else SuccessResult(it, event)
                                }.onErrorReturn { ErrorEffectResult(it, event) }
                                .startWith(LoadingEffectResult(event))
                    }
                    .distinctUntilChanged()
                    .share()

    private fun stateReducer(): BiFunction<PModel<S, I>, SuccessResult<R, I>, PModel<S, I>> =
            BiFunction { currentUIModel, result ->
                SuccessState(stateReducer(result.bundle, currentUIModel.bundle), result.event)
            }

    private fun effectReducer(): BiFunction<PEffect<E, I>, in EffectResult<E, I>, PEffect<E, I>> =
            BiFunction { currentUIModel, result ->
                result.run {
                    when (this) {
                        is LoadingEffectResult -> LoadingEffect(currentUIModel.bundle, event)
                        is SuccessEffectResult -> successEffect(currentUIModel)
                        is ErrorEffectResult -> errorEffect(currentUIModel)
                    }
                }
            }

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
}
