package com.zeyad.rxredux

import org.mockito.Mockito

fun <T> anyObject(): T {
    return Mockito.anyObject<T>()
}