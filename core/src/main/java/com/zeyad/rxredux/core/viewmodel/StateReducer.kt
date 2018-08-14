package com.zeyad.rxredux.core.viewmodel

/**
 * @author Zeyad Gasser.
 */
interface StateReducer<S> {
    fun reduce(newResult: Any, event: String, currentStateBundle: S?): S
}