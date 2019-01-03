package com.zeyad.rxredux.core.viewmodel

import com.jakewharton.rx.ReplayingShare
import com.zeyad.rxredux.core.*
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableTransformer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

interface IBaseViewModel<S> {

    fun stateReducer(): (newResult: Any, event: BaseEvent<*>, currentStateBundle: S) -> S

    fun mapEventsToActions(): Function<BaseEvent<*>, Flowable<*>>

    fun middleware(): (PModel<S>) -> Unit = { Unit }

    fun store(events: Observable<BaseEvent<*>>, initialState: S): Flowable<PModel<S>> =
            events.toFlowable(BackpressureStrategy.BUFFER)
                    .compose<PModel<S>>(pModelsTransformer(initialState))
                    .compose(ReplayingShare.instance())
                    .doAfterNext {
                        when (it) {
                            is SuccessState, is LoadingState -> Timber.d("PModel: $it")
                            is ErrorState -> Timber.e(it.error, "Error")
                        }
                        middleware().invoke(it)
                    }

    private fun pModelsTransformer(initialState: S): FlowableTransformer<BaseEvent<*>, PModel<S>> =
            FlowableTransformer { events ->
                events.observeOn(Schedulers.computation())
                        .distinctUntilChanged { e1: BaseEvent<*>, e2: BaseEvent<*> -> e1 == e2 }
                        .concatMap { event ->
                            Flowable.just(event)
                                    .concatMap(mapEventsToActions())
                                    .compose(mapActionsToResults(event))
                        }
                        .distinctUntilChanged { r1: Result<*>, r2: Result<*> -> r1 == r2 }
                        .scan<PModel<S>>(SuccessState(initialState), reducer())
                        .distinctUntilChanged { m1: PModel<S>, m2: PModel<S> -> m1 == m2 }
                        .observeOn(AndroidSchedulers.mainThread())
            }

    private fun mapActionsToResults(event: BaseEvent<*>): FlowableTransformer<Any, Result<*>> =
            FlowableTransformer { action ->
                action.map<Result<*>> { SuccessResult(it, event) }
                        .onErrorReturn { ErrorResult(it, event) }
                        .startWith(LoadingResult(event))
            }

    private fun reducer(): BiFunction<PModel<S>, Result<*>, PModel<S>> =
            BiFunction { currentUIModel, result ->
                result.run {
                    when (this) {
                        is LoadingResult -> loadingState(currentUIModel)
                        is ErrorResult -> errorState(currentUIModel)
                        is SuccessResult<*> -> successState(currentUIModel)
                    }
                }
            }

    private fun SuccessResult<*>.successState(currentUIModel: PModel<S>): SuccessState<S> =
            when (currentUIModel) {
                is SuccessState, is LoadingState ->
                    SuccessState(stateReducer().invoke(bundle!!, event, currentUIModel.bundle), event)
                is ErrorState -> throw IllegalStateException(makeMsg(currentUIModel, this))
            }

    private fun ErrorResult.errorState(currentUIModel: PModel<S>): ErrorState<S> =
            when (currentUIModel) {
                is LoadingState -> ErrorState(error, currentUIModel.bundle, event)
                is SuccessState, is ErrorState ->
                    throw IllegalStateException(makeMsg(currentUIModel, this))
            }

    private fun Result<*>.loadingState(currentUIModel: PModel<S>): LoadingState<S> =
            when (currentUIModel) {
                is SuccessState, is ErrorState -> LoadingState(currentUIModel.bundle, event)
                is LoadingState ->
                    throw IllegalStateException(makeMsg(currentUIModel, this))
            }

    private fun makeMsg(currentUIModel: PModel<S>, result: Result<*>) =
            "Can not reduce from $currentUIModel to ${currentUIModel::class.java.simpleName} with $result"
}
