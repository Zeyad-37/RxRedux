package com.zeyad.rxredux.screens.navigation

import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.EmptyEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import com.zeyad.rxredux.screens.user.detail.NavigateToEvent
import com.zeyad.rxredux.screens.user.list.GetPaginatedUsersEvent
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable

class FirstVM : BaseViewModel<FirstState>() {
    override var disposable: CompositeDisposable = CompositeDisposable()

    override fun stateReducer(newResult: Any, event: BaseEvent<*>, currentStateBundle: FirstState): FirstState {
        return when (newResult) {
            is Long -> FullFirstState
            else -> throw IllegalStateException("Wuut ?")
        }
    }

    override fun mapEventsToActions(event: BaseEvent<*>): Flowable<*> {
        return when (event) {
            is EmptyEvent -> Flowable.error<Any>(RuntimeException("Forced Error"))
            is NavigateToEvent -> Flowable.just(SuccessEffectResult(event.getPayLoad(), event))
            is GetPaginatedUsersEvent -> Flowable.just(event.getPayLoad())
            else -> throw IllegalStateException("Waaat ?")
        }
    }

    override fun errorMessageFactory(throwable: Throwable, event: BaseEvent<*>): String {
        return throwable.localizedMessage
    }
}