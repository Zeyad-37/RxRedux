package com.zeyad.rxredux.core.eventbus

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.PublishSubject

/**
 * @author Zeyad Gasser.
 */
internal class RxEventBus(private val rxBus: PublishSubject<Any> = PublishSubject.create(),
                          val backPressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER) : IRxEventBus<Any> {

    override fun send(t: Any) = rxBus.onNext(t)

    override fun toFlowable(): Flowable<Any> = rxBus.toFlowable(backPressureStrategy)

    override fun hasFlowables(): Boolean = rxBus.hasObservers()

    companion object : SingletonHolder<BackpressureStrategy, IRxEventBus<Any>>({
        RxEventBus(backPressureStrategy = it)
    })
}
