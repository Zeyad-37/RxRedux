package com.zeyad.rxredux.simplevm

import android.graphics.Color
import com.zeyad.rxredux.AnalyticsTracker
import com.zeyad.rxredux.core.vm.SimpleRxViewModel
import com.zeyad.rxredux.core.vm.Tracker
import io.reactivex.Flowable
import java.util.concurrent.TimeUnit

class MySimpleVm : SimpleRxViewModel<MyInput, MyState, MyEffect>() {

    override fun handleInputs(input: MyInput): Flowable<RxOutcome> {
        return when (input) {
            is ChangeBackgroundButtonClickInput -> {
                return Flowable.just(outcomes.state(getBackgroundState(input)))
                        .delay(3, TimeUnit.SECONDS)
            }
            is ShowDialogButtonClickInput -> Flowable.just(ShowDialogEffect).asyncOutcome()
            is ErrorInput -> Flowable.just(outcomes.error(java.lang.IllegalStateException("test")))
        }
    }

    private fun getBackgroundState(input: ChangeBackgroundButtonClickInput) = ColorBackgroundState(Color.argb(
            255, input.r, input.g, input.b
    ))

    override fun track(): Tracker = {
        states { state, _ -> AnalyticsTracker.sendEvent(state) }
    }
}
