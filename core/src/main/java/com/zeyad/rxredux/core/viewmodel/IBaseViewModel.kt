package com.zeyad.rxredux.core.viewmodel

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

interface IBaseViewModel<S> {

    var disposable: CompositeDisposable

    fun stateReducer(newResult: Any, event: BaseEvent<*>, currentStateBundle: S): S

    fun mapEventsToActions(event: BaseEvent<*>): Flowable<*>

    fun errorMessageFactory(throwable: Throwable, event: BaseEvent<*>): String

    fun effectsMiddleware(it: PEffect<*>) {
        when (it) {
            is SuccessEffect, is LoadingEffect -> Log.d("IBaseViewModel", "PEffect: $it")
            is ErrorEffect -> {
                Log.d("IBaseViewModel", "PEffect: $it")
                Log.e("IBaseViewModel", "Error", it.error)
            }
        }
    }

    fun stateMiddleware(it: SuccessState<S>) = Log.d("IBaseViewModel", "PModel: $it")

    fun store(events: Observable<BaseEvent<*>>, initialState: S): Pair<MutableLiveData<SuccessState<S>>, MutableLiveData<PEffect<*>>> {
        val pModels = events.toFlowable(BackpressureStrategy.BUFFER)
                .toResult()
                .publish()
                .autoConnect(0) // { disposable.add(it) }
        return Pair(statesLiveData(pModels, initialState), effectsLiveData(pModels))
    }

    fun statesLiveData(pModels: Flowable<Result<*>>, initialState: S): MutableLiveData<SuccessState<S>> {
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

    fun effectsLiveData(pModels: Flowable<Result<*>>): MutableLiveData<PEffect<*>> {
        val liveEffects = MutableLiveData<PEffect<*>>()
        val effects = PublishSubject.create<PEffect<*>>()
        pModels.filter { it is EffectResult }
                .map { it as EffectResult }
                .scan<PEffect<*>>(SuccessEffect(Unit, EmptyEvent), effectReducer())
                .distinctUntilChanged { m1: PEffect<*>, m2: PEffect<*> -> m1 == m2 }
                .doAfterNext { effectsMiddleware(it) }
                .toObservable()
                .subscribe(effects)
        disposable.add(effects.subscribe { effect: PEffect<*> -> liveEffects.postValue(effect) })
        return liveEffects
    }

    private fun Flowable<BaseEvent<*>>.toResult(): Flowable<Result<*>> {
        return observeOn(Schedulers.computation())
                .distinctUntilChanged { e1: BaseEvent<*>, e2: BaseEvent<*> -> e1 == e2 }
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
                SuccessState(stateReducer(result.bundle!!, result.event, currentUIModel.bundle), result.event)
            }

    private fun effectReducer(): BiFunction<PEffect<*>, EffectResult<*>, PEffect<*>> =
            BiFunction { currentUIModel, result ->
                result.run {
                    when {
                        this is SuccessEffectResult -> successEffect(currentUIModel)
                        this is ErrorEffectResult -> errorState(currentUIModel)
                        this is LoadingEffectResult -> LoadingEffect(currentUIModel.bundle, event)
                        else -> throwIllegalStateException(currentUIModel, result)
                    }
                }
            }

    private fun SuccessEffectResult<*>.successEffect(currentUIModel: PEffect<*>): SuccessEffect<*> =
            when (currentUIModel) {
                is LoadingEffect -> SuccessEffect(bundle, event)
                is SuccessEffect, is ErrorEffect -> throwIllegalStateException(currentUIModel, this)
            }

    private fun ErrorEffectResult.errorState(currentUIModel: PEffect<*>): ErrorEffect<*> =
            when (currentUIModel) {
                is LoadingEffect -> ErrorEffect(error, errorMessageFactory(error, event), currentUIModel.bundle, event)
                is SuccessEffect, is ErrorEffect -> throwIllegalStateException(currentUIModel, this)
            }

    private fun throwIllegalStateException(currentUIModel: PModel<*>, result: Result<*>): Nothing =
            throw IllegalStateException("Can not reduce from $currentUIModel to ${currentUIModel::class.java.simpleName} with $result")
}
