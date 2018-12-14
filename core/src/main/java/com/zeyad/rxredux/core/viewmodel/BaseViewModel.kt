package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.ViewModel
import android.util.Log
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

abstract class BaseViewModel<S> : ViewModel() {
    abstract fun stateReducer(): (newResult: Any, event: BaseEvent<*>, currentStateBundle: S) -> S

    abstract fun mapEventsToActions(): Function<BaseEvent<*>, Flowable<*>>

    open fun middleware(): (UIModel<S>) -> Unit = { Unit }

    fun processEvents(events: Observable<BaseEvent<*>>, initialState: S): Flowable<UIModel<S>> =
            events.toFlowable(BackpressureStrategy.BUFFER)
                    .compose<UIModel<S>>(uiModelsTransformer(initialState))
                    .doAfterNext {
                        when (it) {
                            is SuccessState, is LoadingState ->
                                Log.d("onNext", "UIModel: " + it.toString())
                            is ErrorState -> Log.e("UIObserver", "onChanged", it.error)
                        }
                        middleware().invoke(it)
                    }
                    .compose(ReplayingShare.instance())

    private fun uiModelsTransformer(initialState: S): FlowableTransformer<BaseEvent<*>, UIModel<S>> =
            FlowableTransformer { events ->
                events.observeOn(Schedulers.computation())
                        .concatMap {
                            Flowable.just(it)
                                    .concatMap(mapEventsToActions())
                                    .compose(mapActionsToResults(it))
                        }
                        .distinctUntilChanged { t1: Result<*>, t2: Result<*> -> t1 == t2 }
                        .scan<UIModel<S>>(SuccessState(initialState), reducer())
                        .distinctUntilChanged { t1: UIModel<*>, t2: UIModel<*> -> t1 == t2 }
                        .observeOn(AndroidSchedulers.mainThread())
            }

    private fun mapActionsToResults(eventName: BaseEvent<*>): FlowableTransformer<Any, Result<*>> =
            FlowableTransformer { it ->
                it.map<Result<*>> { SuccessResult(it, eventName) }
                        .onErrorReturn { ErrorResult(it, eventName) }
                        .startWith(LoadingResult(eventName))
            }

    private fun reducer(): BiFunction<UIModel<S>, Result<*>, UIModel<S>> =
            BiFunction { currentUIModel, result ->
                when (result) {
                    is LoadingResult -> when (currentUIModel) {
                        is LoadingState ->
                            throw IllegalStateException(getErrorMessage(currentUIModel, result, LOADING_STATE))
                        is SuccessState -> LoadingState(currentUIModel.bundle, result.event)
                        is ErrorState -> LoadingState(currentUIModel.bundle, result.event)
                    }
                    is ErrorResult -> when (currentUIModel) {
                        is LoadingState -> ErrorState(currentUIModel.bundle, result.error, result.event)
                        is SuccessState ->
                            throw IllegalStateException(getErrorMessage(currentUIModel, result, SUCCESS_STATE))
                        is ErrorState ->
                            throw IllegalStateException(getErrorMessage(currentUIModel, result, ERROR_STATE))
                    }
                    is SuccessResult<*> -> when (currentUIModel) {
                        is SuccessState ->
                            SuccessState(stateReducer().invoke(result.bundle!!, result.event,
                                    currentUIModel.bundle), result.event)
                        is LoadingState -> SuccessState(stateReducer().invoke(result.bundle!!,
                                result.event, currentUIModel.bundle), result.event)
                        is ErrorState ->
                            throw IllegalStateException(getErrorMessage(currentUIModel, result, ERROR_STATE))
                    }
                }
            }

    private fun getErrorMessage(currentUIModel: UIModel<S>, result: Result<*>, nextState: String) =
            "Can not reduce from $currentUIModel to $nextState with $result"

    companion object {
        const val ERROR_STATE = "ErrorsState"
        const val SUCCESS_STATE = "SuccessState"
        const val LOADING_STATE = "LoadingState"
    }
}
