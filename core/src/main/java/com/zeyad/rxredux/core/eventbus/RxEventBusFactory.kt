package com.zeyad.rxredux.core.eventbus

import io.reactivex.BackpressureStrategy

@Deprecated("Use your own implementation")
object RxIntentBusFactory {
    fun getInstance(backPressureStrategy: BackpressureStrategy = BackpressureStrategy.BUFFER):
            IRxIntentBus<Any> = RxIntentBus.getInstance(backPressureStrategy)
}
