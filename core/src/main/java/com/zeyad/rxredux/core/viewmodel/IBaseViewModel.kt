package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.jakewharton.rx.ReplayingShare
import com.zeyad.rxredux.core.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

interface IBaseViewModel<S, E> {

    var disposable: CompositeDisposable

    fun reducer(newResult: Any, event: BaseEvent<*>, currentStateBundle: S): S

    fun mapEventsToActions(event: BaseEvent<*>): Flowable<*>

    fun errorMessageFactory(throwable: Throwable, event: BaseEvent<*>): Message

    fun effectsMiddleware(it: PEffect<*>) {
        Log.d("IBaseViewModel", "PEffect: $it")
        if (it is ErrorEffect) Log.e("IBaseViewModel", "Error", it.error)
    }

    fun stateMiddleware(it: SuccessState<S>) {
        Log.d("IBaseViewModel", "PModel: $it")
    }

    fun store(events: Observable<BaseEvent<*>>, initialState: S): Pair<LiveData<SuccessState<S>>, LiveData<PEffect<E>>> {
        val pModels = events.toFlowable(BackpressureStrategy.BUFFER)
                .toResult()
                .publish()
                .autoConnect(0)
        return Pair(statesLiveData(pModels, initialState), effectsLiveData(pModels as Flowable<Result<E>>))
    }

    private fun statesLiveData(pModels: Flowable<Result<*>>, initialState: S): MutableLiveData<SuccessState<S>> {
        val liveState = MutableLiveData<SuccessState<S>>()
        disposable.add(pModels
                .filter { it is SuccessResult }
                .map { it as SuccessResult }
                .scan<SuccessState<S>>(SuccessState(initialState), stateReducer())
                .distinctUntilChanged { m1: SuccessState<*>, m2: SuccessState<*> -> m1 == m2 }
                .doAfterNext { stateMiddleware(it) }
                .compose(ReplayingShare.instance())
                .subscribe { successState: SuccessState<S> -> liveState.postValue(successState) })
        return liveState
    }

    private fun effectsLiveData(pModels: Flowable<Result<E>>): MutableLiveData<PEffect<E>> {
        val liveEffects = MutableLiveData<PEffect<E>>()
        val effects = PublishSubject.create<PEffect<E>>()
        pModels.filter { it is EffectResult }
                .map { it as EffectResult }
                .scan<PEffect<E>>(SuccessEffect(Any() as E, EmptyEvent), effectReducer())
                .distinctUntilChanged { m1: PEffect<E>, m2: PEffect<E> -> m1 == m2 }
                .doAfterNext { effectsMiddleware(it) }
                .toObservable()
                .subscribe(effects)
        disposable.add(effects.subscribe { effect: PEffect<E> -> liveEffects.postValue(effect) })
        return liveEffects
    }

    private fun Flowable<BaseEvent<*>>.toResult(): Flowable<Result<*>> {
        return observeOn(Schedulers.computation())
                .concatMap { event ->
                    Flowable.just(event)
                            .concatMap { mapEventsToActions(it) }
                            .map<Result<*>> {
                                if (it is EffectResult<*>) it
                                else SuccessResult(it, event)
                            }.onErrorReturn { ErrorEffectResult(it, event) }
                            .startWith(LoadingEffectResult(event))
                }
                .distinctUntilChanged()
    }

    private fun stateReducer(): BiFunction<SuccessState<S>, SuccessResult<*>, SuccessState<S>> =
            BiFunction { currentUIModel, result ->
                SuccessState(reducer(result.bundle!!, result.event, currentUIModel.bundle), result.event)
            }

    private fun effectReducer(): BiFunction<PEffect<E>, EffectResult<E>, PEffect<E>> =
            BiFunction { currentUIModel, result ->
                result.run {
                    when {
                        this is SuccessEffectResult -> successEffect(currentUIModel)
                        this is ErrorEffectResult -> errorEffect(currentUIModel)
                        this is LoadingEffectResult -> LoadingEffect(currentUIModel.bundle, event)
                        else -> throwIllegalStateException(currentUIModel, result)
                    }
                }
            }

    private fun SuccessEffectResult<E>.successEffect(currentUIModel: PEffect<*>): SuccessEffect<E> =
            when (currentUIModel) {
                is LoadingEffect -> SuccessEffect(bundle, event)
                is SuccessEffect, is ErrorEffect -> throwIllegalStateException(currentUIModel, this)
            }

    private fun ErrorEffectResult.errorEffect(currentUIModel: PEffect<E>): ErrorEffect<E> =
            when (currentUIModel) {
                is LoadingEffect -> ErrorEffect(error, errorMessageFactory(error, event), currentUIModel.bundle, event)
                is SuccessEffect, is ErrorEffect -> throwIllegalStateException(currentUIModel, this)
            }

    private fun throwIllegalStateException(currentUIModel: PModel<*>, result: Result<*>): Nothing =
            throw IllegalStateException("Can not reduce from $currentUIModel to ${currentUIModel::class.java.simpleName} with $result")
}
