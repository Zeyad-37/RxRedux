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
                        is LoadingResult -> when (currentUIModel) {
                            is LoadingState ->
                                throw IllegalStateException(makeMsg(currentUIModel, this, LOADING_STATE))
                            is SuccessState -> LoadingState(currentUIModel.bundle, event)
                            is ErrorState -> LoadingState(currentUIModel.bundle, event)
                        }
                        is ErrorResult -> when (currentUIModel) {
                            is LoadingState -> ErrorState(currentUIModel.bundle, error, event)
                            is SuccessState ->
                                throw IllegalStateException(makeMsg(currentUIModel, this, SUCCESS_STATE))
                            is ErrorState ->
                                throw IllegalStateException(makeMsg(currentUIModel, this, ERROR_STATE))
                        }
                        is SuccessResult<*> -> when (currentUIModel) {
                            is SuccessState -> SuccessState(stateReducer()
                                    .invoke(bundle!!, event, currentUIModel.bundle), event)
                            is LoadingState -> SuccessState(stateReducer()
                                    .invoke(bundle!!, event, currentUIModel.bundle), event)
                            is ErrorState ->
                                throw IllegalStateException(makeMsg(currentUIModel, this, ERROR_STATE))
                        }
                    }
                }
            }

    private fun makeMsg(currentUIModel: PModel<S>, result: Result<*>, nextState: String) =
            "Can not reduce from $currentUIModel to $nextState with $result"

    companion object {
        const val ERROR_STATE = "ErrorsState"
        const val SUCCESS_STATE = "SuccessState"
        const val LOADING_STATE = "LoadingState"
    }
}