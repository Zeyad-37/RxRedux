package com.zeyad.rxredux.core.eventbus

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

internal class RxIntentBus(private val rxBus: PublishSubject<Any> = PublishSubject.create(),
                          val backPressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER) : IRxIntentBus<Any> {

    private val flowable = rxBus.toFlowable(backPressureStrategy).share()

    override fun send(t: Any) = rxBus.onNext(t)

    override fun observe(): Flowable<Any> = flowable

    companion object : SingletonHolder<BackpressureStrategy, IRxIntentBus<Any>>({
        RxIntentBus(backPressureStrategy = it)
    })
}
