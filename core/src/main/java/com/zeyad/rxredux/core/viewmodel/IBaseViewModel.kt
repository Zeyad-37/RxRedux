package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Parcelable
import android.util.Log
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

interface IBaseViewModel<R, S : Parcelable, E> {

    var disposables: CompositeDisposable

    val currentStateStream: BehaviorSubject<Any>

    fun stateReducer(newResult: R, currentState: S): S

    fun reduceEventsToResults(event: BaseEvent<*>, currentState: Any): Flowable<*>

    fun errorMessageFactory(throwable: Throwable, event: BaseEvent<*>, currentStateBundle: E): Message =
            StringMessage(throwable.localizedMessage)

    fun middleware(it: PModel<*>) {
        if (it is ErrorEffect) Log.e("IBaseViewModel", "Error", it.error)
        else Log.d("IBaseViewModel", "PModel: $it")
    }

    fun store(events: Observable<BaseEvent<*>>, initialState: S): LiveData<PModel<*>> {
        currentStateStream.onNext(initialState)
        val pModels = events.toFlowable(BackpressureStrategy.BUFFER)
                .toResult()
                .publish()
                .autoConnect(0)
        val liveState = MutableLiveData<PModel<*>>()
        val states = stateStream(pModels as Flowable<Result<R>>, initialState)
        val effects = effectStream(pModels as Flowable<Result<E>>)
        Flowable.merge(states, effects)
                .doAfterNext { middleware(it) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { t: PModel<*> -> liveState.value = t }
                .let { disposables.add(it) }
        return liveState
    }

    private fun stateStream(pModels: Flowable<Result<R>>, initialState: S): Flowable<PModel<*>> =
            pModels.filter { it is SuccessResult }
                    .map { it as SuccessResult }
                    .scan<PModel<S>>(SuccessState(initialState), stateReducer())
                    .map {
                        if (currentStateStream.value == it.bundle && initialState != it.bundle) {
                            EmptySuccessState()
                        } else {
                            currentStateStream.onNext(it.bundle)
                            it
                        }
                    }
                    .compose(ReplayingShare.instance())

    private fun effectStream(pModels: Flowable<Result<E>>): Flowable<PModel<*>> =
            pModels.filter { it is EffectResult }
                    .map { it as EffectResult }
                    .scan<PModel<*>>(EmptySuccessEffect(), effectReducer())
                    .filter { t: PModel<*> -> t !is EmptySuccessEffect }
                    .distinctUntilChanged()
                    .doOnNext {
                        if (it is SuccessEffectResult<*>) {
                            currentStateStream.onNext(it.bundle!!)
                        }
                    }


    private fun Flowable<BaseEvent<*>>.toResult(): Flowable<Result<*>> =
            observeOn(Schedulers.computation())
                    .concatMap { event ->
                        Log.d("IBaseViewModel", "Event: $event")
                        reduceEventsToResults(event, currentStateStream.value!!)
                                .map<Result<*>> {
                                    if (it is EffectResult<*>) it
                                    else SuccessResult(it, event)
                                }.onErrorReturn { ErrorEffectResult(it, event) }
                                .startWith(LoadingEffectResult(event))
                    }
                    .distinctUntilChanged()

    private fun stateReducer(): BiFunction<PModel<S>, SuccessResult<R>, PModel<S>> =
            BiFunction { currentUIModel, result ->
                SuccessState(stateReducer(result.bundle, currentUIModel.bundle), result.event)
            }

    private fun effectReducer(): BiFunction<PModel<*>, in EffectResult<E>, PModel<*>> =
            BiFunction { currentUIModel, result ->
                result.run {
                    when (this) {
                        is LoadingEffectResult -> LoadingEffect(currentUIModel.bundle, event)
                        is SuccessEffectResult -> successEffect(currentUIModel as PEffect<E>)
                        is ErrorEffectResult -> errorEffect(currentUIModel as PEffect<E>)
                    }
                }
            }

    private fun SuccessEffectResult<E>.successEffect(currentUIModel: PEffect<E>): SuccessEffect<E> =
            when (currentUIModel) {
                is LoadingEffect -> SuccessEffect(bundle, event)
                is EmptySuccessEffect, is SuccessEffect, is ErrorEffect -> currentUIModel.throwIllegalStateException(this)
            }

    private fun ErrorEffectResult.errorEffect(currentUIModel: PEffect<E>): ErrorEffect<E> =
            when (currentUIModel) {
                is LoadingEffect -> ErrorEffect(error, errorMessageFactory(error, event, currentUIModel.bundle),
                        currentUIModel.bundle, event)
                is EmptySuccessEffect, is SuccessEffect, is ErrorEffect -> currentUIModel.throwIllegalStateException(this)
            }
}
