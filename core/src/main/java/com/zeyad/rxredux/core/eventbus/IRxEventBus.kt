package com.zeyad.rxredux.core.eventbus

import io.reactivex.Flowable
import io.reactivex.annotations.NonNull

/**
 * @author ZIaDo on 2/26/18.
 */
interface IRxEventBus<T> {
    fun send(@NonNull t: T)

    @NonNull
    fun toFlowable(): Flowable<T>

    fun hasFlowables(): Boolean
}