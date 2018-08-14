package com.zeyad.rxredux.core

/**
 * @author Zeyad Gasser.
 */
interface BaseEvent<T> {
    fun getPayLoad(): T
}