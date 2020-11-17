package com.zeyad.rxredux.core.v2

import io.reactivex.Flowable

interface InputHandler<I : Input, S : State> {
    fun handleInputs(input: I, currentState: S): Flowable<RxOutcome>
}
