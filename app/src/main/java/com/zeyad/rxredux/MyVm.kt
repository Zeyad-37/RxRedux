package com.zeyad.rxredux

import com.zeyad.rxredux.core.vm.RxViewModel
import com.zeyad.rxredux.core.vm.Tracker
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class MyVm : RxViewModel<MyInput, MyState, MyResult, MyEffect>(MyReducer()) {

    override fun handleInputs(input: MyInput): Flowable<RxOutcome> {
        return when (input) {
            is ChangeBackgroundButtonClickInput -> changeBackground().map { outcomes.result(it) }
            is ShowDialogButtonClickInput -> Flowable.just(ShowDialogEffect).asyncOutcome()
            is ErrorInput -> Flowable.just(outcomes.error(java.lang.IllegalStateException("test")))
        }
    }

    private fun changeBackground() = Flowable.just(ChangeBackgroundResult).delay(3, TimeUnit.SECONDS)

    override fun track(): Tracker = {
        states { state, _ -> AnalyticsTracker.sendEvent(state) }
    }
}
