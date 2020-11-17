package com.zeyad.rxredux.core.v2

import io.reactivex.Flowable

fun Flowable<RxOutcome>.executeInParallel(): AsyncOutcomeFlowable = AsyncOutcomeFlowable(this)

fun <E : Effect> E.toEffectOutcome(): RxOutcome = RxReduxViewModel.RxEffect(this)

fun <E : Effect> E.toEffectOutcomeFlowable(): Flowable<RxOutcome> = toEffectOutcome().toFlowable()

fun <R : Result> R.toResultOutcome(): RxOutcome = RxReduxViewModel.RxResult(this)

fun <R : Result> R.toResultOutcomeFlowable(): Flowable<RxOutcome> = toResultOutcome().toFlowable()

fun Throwable.toErrorOutcome(errorMessage: String? = null): RxOutcome =
        RxError(Error(errorMessage ?: message.orEmpty(), this))

fun Throwable.toErrorOutcomeFlowable(): Flowable<RxOutcome> = toErrorOutcome().toFlowable()

fun RxOutcome.toFlowable() = Flowable.just(this)
