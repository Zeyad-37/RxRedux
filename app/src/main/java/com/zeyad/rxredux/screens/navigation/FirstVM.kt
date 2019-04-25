package com.zeyad.rxredux.screens.navigation

import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.EmptyEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import com.zeyad.rxredux.screens.detail.NavigateToEvent
import com.zeyad.rxredux.screens.list.GetPaginatedUsersEvent
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable

class FirstVM(override var disposables: CompositeDisposable = CompositeDisposable()) : BaseViewModel<Any, FirstState, FirstEffect>() {

    override fun reducer(newResult: Any, currentStateBundle: FirstState): FirstState {
        return when (newResult) {
            is Long -> FullFirstState
            else -> throw IllegalStateException("Wuut ?")
        }
    }

    override fun reduceEventsToResults(event: BaseEvent<*>, currentStateBundle: Any): Flowable<*> {
        return when (event) {
            is EmptyEvent -> Flowable.error<Any>(RuntimeException("Forced Error"))
            is NavigateToEvent -> Flowable.just(SuccessEffectResult(NavigateToEffect(event.getPayLoad()), event))
            is GetPaginatedUsersEvent -> Flowable.just(event.getPayLoad())
            else -> throw IllegalStateException("Waaat ?")
        }
    }
}
