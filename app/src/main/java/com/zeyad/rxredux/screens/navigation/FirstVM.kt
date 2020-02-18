package com.zeyad.rxredux.screens.navigation

import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import com.zeyad.rxredux.screens.detail.NavigateToIntent
import com.zeyad.rxredux.screens.list.GetPaginatedUsersIntent
import io.reactivex.Flowable

class FirstVM : BaseViewModel<Any, Any, FirstState, FirstEffect>() {

    override fun stateReducer(newResult: Any, currentState: FirstState): FirstState {
        return when (newResult) {
            is Long -> FullFirstState
            else -> throw IllegalStateException("Wuut ?")
        }
    }

    override fun reduceIntentsToResults(intent: Any, currentState: FirstState): Flowable<*> {
        return when (intent) {
            is NavigateToIntent -> Flowable.just(SuccessEffectResult(NavigateToEffect(intent.intent), intent))
            is GetPaginatedUsersIntent -> Flowable.just(intent.lastId)
            else -> Flowable.error<Any>(RuntimeException("Forced Error"))
        }
    }
}
