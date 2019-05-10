package com.zeyad.rxredux.core

interface BaseEvent<T> {
    fun getPayLoad(): T
}

object EmptyEvent : BaseEvent<Unit> {
    override fun getPayLoad() = Unit
}

interface RootVertex
interface LeafVertex
