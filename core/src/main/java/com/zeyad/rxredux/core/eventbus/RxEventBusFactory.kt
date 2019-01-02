package com.zeyad.rxredux.core.eventbus

import io.reactivex.BackpressureStrategy

object RxEventBusFactory {
    fun getInstance(backPressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER):
            IRxEventBus<Any> = RxEventBus.getInstance(backPressureStrategy)
}
