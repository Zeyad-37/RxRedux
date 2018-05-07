package com.zeyad.rxredux.core.eventbus

import io.reactivex.BackpressureStrategy

/**
 * @author ZIaDo on 2/26/18.
 */
class RxEventBusFactory {
    companion object {
        fun getInstance(backpressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER):
                IRxEventBus<Any> = RxEventBus.getInstance(backpressureStrategy)
    }
}