package com.zeyad.rxredux.core.utils

import android.content.Context

sealed class Either<out A, out B> {
    class Left<A>(val value: A) : Either<A, Nothing>()
    class Right<B>(val value: B) : Either<Nothing, B>()

    fun isRight(): Boolean = when (this) {
        is Either.Left -> false
        is Either.Right -> true
    }

    fun isLeft(): Boolean = when (this) {
        is Either.Left -> true
        is Either.Right -> false
    }
}

fun Either<String, Int>.getErrorMessage(context: Context): String {
    return when (this) {
        is Either.Left<String> -> value
        is Either.Right<Int> -> context.getString(value)
    }
}

fun Either<String, Int>.getLeft(): String {
    return when (this) {
        is Either.Left<String> -> value
        is Either.Right<Int> -> throw IllegalAccessException("This is the Right not left!")
    }
}
