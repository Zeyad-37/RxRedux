package com.zeyad.rxredux.core.vm

import com.zeyad.rxredux.core.vm.rxvm.Effect
import com.zeyad.rxredux.core.vm.rxvm.Error
import com.zeyad.rxredux.core.vm.rxvm.Input
import com.zeyad.rxredux.core.vm.rxvm.Result
import com.zeyad.rxredux.core.vm.rxvm.State
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

abstract class RxViewModel<I : Input, S : State, R : Result, E : Effect>(
    private val reducer: Reducer<S, R>
) : BaseRxViewModel<I, S, R, E>() {

    val outcomes: RxOutcomes = RxOutcomes()

    inner class RxOutcomes {
        fun effect(effect: E): RxOutcome = RxEffect(effect)

        fun empty(): RxOutcome = RxEmpty

        fun error(error: Throwable): RxOutcome = RxError(Error(error.message.orEmpty(), error))

        fun result(result: R): RxOutcome = RxResult(result)
    }

    override fun processOutcomes(outcomes: Flowable<RxOutcome>) {
        disposable = outcomes
            .doOnNext {
                trackEvents(it)
                logEvents(it)
            }
            .observeOn(AndroidSchedulers.mainThread())
            .map {
                val result = processResult(it)
                handleResult(result)
                result
            }
            .subscribe()
    }

    private fun processResult(outcome: RxOutcome): RxOutcome {
        return if (outcome is RxResult) {
            val newState = reducer.reduceStateInternal(RxState(currentState), outcome)
            trackState(newState.state, outcome.input)
            logState(newState.state)
            newState
        } else {
            outcome
        }
    }

    private fun Reducer<S, R>.reduceStateInternal(state: RxState, rxResult: RxResult): RxState =
        RxState(reduce(state.state as S, rxResult.result as R), rxResult.input)
}
