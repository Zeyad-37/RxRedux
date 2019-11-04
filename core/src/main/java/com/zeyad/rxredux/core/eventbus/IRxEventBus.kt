package com.zeyad.rxredux.core.eventbus

import io.reactivex.Flowable

@Deprecated("Use your own implementation")
interface IRxIntentBus<T> {
    fun send(t: T)

    fun observe(): Flowable<T>
}
