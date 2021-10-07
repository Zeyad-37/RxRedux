package com.zeyad.rxredux.core.vm

import com.zeyad.rxredux.core.vm.rxvm.Effect
import com.zeyad.rxredux.core.vm.rxvm.EmptyResult
import com.zeyad.rxredux.core.vm.rxvm.Error
import com.zeyad.rxredux.core.vm.rxvm.Input
import com.zeyad.rxredux.core.vm.rxvm.State
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

abstract class SimpleRxViewModel<I : Input, S : State, E : Effect> : BaseRxViewModel<I, S, EmptyResult, E>() {
    val outcomes: RxOutcomes = RxOutcomes()

    open inner class RxOutcomes {
        fun effect(effect: E): RxOutcome = RxEffect(effect)

        fun empty(): RxOutcome = RxEmpty

        fun error(error: Throwable): RxOutcome = RxError(Error(error.message.orEmpty(), error))

        fun state(state: S): RxOutcome = RxState(state)
    }

    override fun processOutcomes(outcomes: Flowable<RxOutcome>) {
        disposable = outcomes
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    trackEvents(it)
                    logEvents(it)

                    if (it is RxState) {
                        trackState(it.state, it.input)
                        logState(it.state)
                    }

                    handleResult(it)
                }
                .subscribe()
    }

    private fun nextState(state: S, input: I): RxOutcome = RxState(state, input)

    // Methods to use to render a new state in VM subclasses in handleInputs method

    // 1. when asynchronous call IS NOT needed to render a new state
    // for instance: Flowable.just(input.newState { renderNewState(input) })
    // where renderNewState is a function returning new State based on input and current state (if applicable)
    @Deprecated("Use outcomes.state() instead")
    fun Input.newState(stateCreator: (input: Input) -> S): RxOutcome =
            nextState(stateCreator.invoke(this), this as I)

    // 2. when asynchronous call IS needed to render a new state
    // for instance: input.newRxState { getChangeBackgroundState(input) }
    // where renderNewState is a function returning Flowable with new State based on input and current state (if applicable)
    @Deprecated("Use outcomes.state() instead")
    fun I.newRxState(stateCreator: (I) -> Flowable<S>): Flowable<RxOutcome> =
            stateCreator.invoke(this).map { nextState(it, this) }
}
