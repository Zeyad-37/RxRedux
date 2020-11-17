package com.zeyad.rxredux.core.v2

import io.reactivex.Flowable
import org.reactivestreams.Subscriber

class AsyncOutcomeFlowable(val flowable: Flowable<RxOutcome>) : Flowable<RxOutcome>() {
    override fun subscribeActual(s: Subscriber<in RxOutcome>?) = Unit
}

data class InputOutcomeStream(val input: Input, val outcomes: Flowable<RxOutcome>)

fun <E : Effect> E.toEffectOutcome(): RxOutcome = IRxViewModel.RxEffect(this)

fun <R : Result> R.toResultOutcome(): RxOutcome = IRxViewModel.RxResult(this)

fun Throwable.toErrorOutcome(errorMessage: String? = null): RxOutcome =
        RxError(Error(errorMessage ?: message.orEmpty(), this))

fun Flowable<RxOutcome>.executeInParallel(): AsyncOutcomeFlowable = AsyncOutcomeFlowable(this)
