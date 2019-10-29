package com.zeyad.rxredux.core.eventbus

import io.reactivex.BackpressureStrategy

object RxIntentBusFactory {
    fun getInstance(backPressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER):
            IRxIntentBus<Any> = RxIntentBus.getInstance(backPressureStrategy)
}
