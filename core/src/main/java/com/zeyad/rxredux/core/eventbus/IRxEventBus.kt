package com.zeyad.rxredux.core.eventbus

import io.reactivex.Flowable

interface IRxEventBus<T> {
    fun send(t: T)

    fun observe(): Flowable<T>
}
