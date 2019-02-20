package com.zeyad.rxredux.core.viewmodel

import com.jakewharton.rx.ReplayingShare
import com.zeyad.rxredux.core.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import timber.log.Timber

interface IBaseViewModel<S> {

    fun stateReducer(newResult: Any, event: BaseEvent<*>, currentStateBundle: S): S

    fun mapEventsToActions(event: BaseEvent<*>): Flowable<*>

    fun errorMessageFactory(throwable: Throwable, event: BaseEvent<*>): String

    fun middleware(): (PModel<S>) -> Unit = {
        when (it) {
            is SuccessState, is LoadingEffect -> Timber.d("PModel: $it")
            is ErrorEffect -> Timber.e(it.error, "Error")
        }
    }

    fun store(events: Observable<BaseEvent<*>>, initialState: S): Pair<Flowable<SuccessState<S>>, Observable<PEffect<S>>> {
        val pModels = events.toFlowable(BackpressureStrategy.BUFFER)
                .toPModel(initialState)
                .publish()
                .autoConnect(0)
        val effects = PublishSubject.create<PEffect<S>>()
        pModels.filter { it is PEffect }
                .map { it as PEffect }
                .doAfterNext { middleware().invoke(it) }
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(effects)
        val states = pModels
                .filter { it is SuccessState }
                .map { it as SuccessState }
                .doAfterNext { middleware().invoke(it) }
                .compose(ReplayingShare.instance())
                .observeOn(AndroidSchedulers.mainThread())
        return Pair<Flowable<SuccessState<S>>, Observable<PEffect<S>>>(states, effects)
    }

    private fun Flowable<BaseEvent<*>>.toPModel(initialState: S): Flowable<PModel<S>> =
            observeOn(Schedulers.computation())
                    .distinctUntilChanged { e1: BaseEvent<*>, e2: BaseEvent<*> -> e1 == e2 }
                    .concatMap { event ->
                        Flowable.just(event)
                                .concatMap { mapEventsToActions(it) }
                                .toResult(event) // Todo break the chain here!
                    }
                    .distinctUntilChanged { r1: Result<*>, r2: Result<*> -> r1 == r2 }
                    .scan<PModel<S>>(SuccessState(initialState), reducer())
                    .distinctUntilChanged { m1: PModel<*>, m2: PModel<*> -> m1 == m2 }

    private fun Flowable<*>.toResult(event: BaseEvent<*>): Flowable<Result<*>> =
            map<Result<*>> {
                if (it is EffectResult<*>) it
                else SuccessResult(it, event)
            }.onErrorReturn { ErrorResult(it, event) }
                    .startWith(LoadingResult(event))

    private fun reducer(): BiFunction<PModel<S>, Result<*>, PModel<S>> =
            BiFunction { currentUIModel, result ->
                result.run {
                    when {
                        this is EffectResult<*> -> SuccessEffect(bundle as S)
                        this is ErrorResult -> errorState(currentUIModel)
                        this is LoadingResult -> loadingState(currentUIModel)
                        this is SuccessResult -> successState(currentUIModel)
                        else -> throw IllegalStateException()
                    }
                }
            }

    private fun SuccessResult<*>.successState(currentUIModel: PModel<S>): SuccessState<S> =
            when (currentUIModel) {
                is SuccessState, is LoadingEffect ->
                    SuccessState(stateReducer(bundle!!, event, currentUIModel.bundle), event)
                is SuccessEffect, is ErrorEffect -> throwIllegalStateException(currentUIModel, this)
            }

    private fun ErrorResult.errorState(currentUIModel: PModel<S>): ErrorEffect<S> =
            when (currentUIModel) {
                is LoadingEffect -> ErrorEffect(error, errorMessageFactory(error, event), currentUIModel.bundle, event)
                is SuccessState, is SuccessEffect, is ErrorEffect -> throwIllegalStateException(currentUIModel, this)
            }

    private fun LoadingResult.loadingState(currentUIModel: PModel<S>): LoadingEffect<S> =
            when (currentUIModel) {
                is SuccessState, is SuccessEffect, is ErrorEffect -> LoadingEffect(currentUIModel.bundle, event)
                is LoadingEffect -> throwIllegalStateException(currentUIModel, this)
            }

    private fun throwIllegalStateException(currentUIModel: PModel<S>, result: Result<*>): Nothing =
            throw IllegalStateException("Can not reduce from $currentUIModel to ${currentUIModel::class.java.simpleName} with $result")
}
