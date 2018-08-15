package com.zeyad.rxredux.core.eventbus

import io.reactivex.Flowable

/**
 * @author Zeyad Gasser.
 */
interface IRxEventBus<T> {
    fun send(t: T)

    fun toFlowable(): Flowable<T>

    fun hasFlowables(): Boolean
}