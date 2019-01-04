package com.zeyad.rxredux.core

interface BaseEvent<T> {
    fun getPayLoad(): T
}

val EmptyEvent = object : BaseEvent<Unit> {
    override fun getPayLoad() = Unit
}