package com.zeyad.rxredux.core

/**
 * @author ZIaDo on 2/27/18.
 */
interface BaseEvent<T> {

    fun getPayLoad(): T
}