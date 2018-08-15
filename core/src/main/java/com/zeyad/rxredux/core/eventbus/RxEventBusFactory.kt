package com.zeyad.rxredux.core.eventbus

import io.reactivex.BackpressureStrategy

/**
 * @author Zeyad Gasser.
 */
class RxEventBusFactory {
    companion object {
        fun getInstance(backPressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER):
                IRxEventBus<Any> = RxEventBus.getInstance(backPressureStrategy)
    }
}