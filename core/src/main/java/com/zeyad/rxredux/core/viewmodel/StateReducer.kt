package com.zeyad.rxredux.core.viewmodel

import com.zeyad.rxredux.core.BaseEvent

/**
 * @author Zeyad Gasser.
 */
interface StateReducer<S> {
    fun reduce(newResult: Any, event: BaseEvent<*>, currentStateBundle: S?): S
}