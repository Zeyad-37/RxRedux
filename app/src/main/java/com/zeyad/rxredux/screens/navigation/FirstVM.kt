package com.zeyad.rxredux.screens.navigation

import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import com.zeyad.rxredux.screens.detail.NavigateToEvent
import com.zeyad.rxredux.screens.list.GetPaginatedUsersEvent
import io.reactivex.Flowable
import io.reactivex.disposables.SerialDisposable

class FirstVM(override var disposable: SerialDisposable = SerialDisposable()) : BaseViewModel<BaseEvent<*>, Any, FirstState, FirstEffect>() {

    override fun stateReducer(newResult: Any, currentState: FirstState): FirstState {
        return when (newResult) {
            is Long -> FullFirstState
            else -> throw IllegalStateException("Wuut ?")
        }
    }

    override fun reduceEventsToResults(event: BaseEvent<*>, currentState: Any): Flowable<*> {
        return when (event) {
            is NavigateToEvent -> Flowable.just(SuccessEffectResult(NavigateToEffect(event.getPayLoad()), event))
            is GetPaginatedUsersEvent -> Flowable.just(event.getPayLoad())
            else -> Flowable.error<Any>(RuntimeException("Forced Error"))
        }
    }
}
