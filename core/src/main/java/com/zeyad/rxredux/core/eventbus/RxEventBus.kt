package com.zeyad.rxredux.core.eventbus

import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.annotations.NonNull
import io.reactivex.subjects.PublishSubject

/**
 * @author ZIaDo on 2/26/18.
 */
internal class RxEventBus(val rxBus: PublishSubject<Any> = PublishSubject.create(),
                          val backpressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER) : IRxEventBus<Any> {
//    @NonNull
//    private val rxBus: PublishSubject<Any>
//    private val backpressureStrategy: BackpressureStrategy

    private constructor() : this(PublishSubject.create(), BackpressureStrategy.BUFFER)

    private constructor(backpressureStrategy: BackpressureStrategy) : this(PublishSubject.create(), backpressureStrategy)

    override fun send(o: Any) {
        rxBus.onNext(o)
    }

    @NonNull
    override fun toFlowable(): Flowable<Any> {
        return rxBus.toFlowable(backpressureStrategy)
    }

    override fun hasFlowables(): Boolean {
        return rxBus.hasObservers()
    }

    companion object {

        private var mInstance: IRxEventBus<Any>? = null

        fun getInstance(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER)
                : IRxEventBus<Any> {
            if (mInstance == null) {
                mInstance = RxEventBus(backpressureStrategy)
            }
            return mInstance as IRxEventBus<Any>
        }
    }
}
