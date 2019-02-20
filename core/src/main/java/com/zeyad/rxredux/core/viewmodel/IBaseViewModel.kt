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
            is SuccessState, is LoadingState -> Timber.d("PModel: $it")
            is ErrorState -> Timber.e(it.error, "Error")
        }
    }

    fun store(events: Observable<BaseEvent<*>>, initialState: S): Pair<Flowable<PState<S>>, Observable<PEffect<S>>> {
        val pModels = events.toFlowable(BackpressureStrategy.BUFFER).toPModel(initialState)
        val effects = PublishSubject.create<PEffect<S>>()
        pModels.flatMap {
            when (it) {
                is PEffect -> Flowable.just(it)
                else -> Flowable.empty()
            }
        }.doAfterNext { middleware().invoke(it) }
                .compose(ReplayingShare.instance())
                .toObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(effects)
        val states = pModels.flatMap {
            when (it) {
                is PState -> Flowable.just(it)
                else -> Flowable.empty()
            }
        }.doAfterNext { middleware().invoke(it) }
                .compose(ReplayingShare.instance())
                .observeOn(AndroidSchedulers.mainThread())
        return Pair<Flowable<PState<S>>, Observable<PEffect<S>>>(states, effects)
    }

    private fun Flowable<BaseEvent<*>>.toPModel(initialState: S): Flowable<PModel<S>> =
            observeOn(Schedulers.computation())
                    .distinctUntilChanged { e1: BaseEvent<*>, e2: BaseEvent<*> -> e1 == e2 }
                    .concatMap { event ->
                        Flowable.just(event)
                                .concatMap { mapEventsToActions(it) }
                                .toResult(event)
                    }
                    .distinctUntilChanged { r1: Result<*>, r2: Result<*> -> r1 == r2 }
                    .scan<PModel<S>>(SuccessState(initialState), reducer())
                    .distinctUntilChanged { m1: PModel<*>, m2: PModel<*> -> m1 == m2 }

    private fun Flowable<*>.toResult(event: BaseEvent<*>): Flowable<Result<*>> =
            map<Result<*>> { SuccessResult(it, event) }
                    .onErrorReturn { ErrorResult(it, event) }
                    .startWith(LoadingResult(event))

    private fun reducer(): BiFunction<PModel<S>, Result<*>, PModel<S>> =
            BiFunction { currentUIModel, result ->
                result.run {
                    when (this) {
                        is ErrorResult -> errorState(currentUIModel)
                        is LoadingResult -> loadingState(currentUIModel)
                        is SuccessResult -> successState(currentUIModel)
                    }
                }
            }

    private fun SuccessResult<*>.successState(currentUIModel: PModel<S>): PModel<S> =
            when (currentUIModel) {
                is SuccessState, is LoadingState ->
                    SuccessState(stateReducer(bundle!!, event, currentUIModel.bundle), event)
                is SuccessEffect -> SuccessEffect(stateReducer(bundle!!, event, currentUIModel.bundle), event)
                is ErrorState -> throwIllegalStateException(currentUIModel, this)
            }

    private fun ErrorResult.errorState(currentUIModel: PModel<S>): ErrorState<S> =
            when (currentUIModel) {
                is LoadingState -> ErrorState(error, errorMessageFactory(error, event), currentUIModel.bundle, event)
                is SuccessState, is SuccessEffect, is ErrorState -> throwIllegalStateException(currentUIModel, this)
            }

    private fun LoadingResult.loadingState(currentUIModel: PModel<S>): LoadingState<S> =
            when (currentUIModel) {
                is SuccessState, is SuccessEffect, is ErrorState -> LoadingState(currentUIModel.bundle, event)
                is LoadingState -> throwIllegalStateException(currentUIModel, this)
            }

    private fun throwIllegalStateException(currentUIModel: PModel<S>, result: Result<*>): Nothing =
            throw IllegalStateException("Can not reduce from $currentUIModel to ${currentUIModel::class.java.simpleName} with $result")
}
