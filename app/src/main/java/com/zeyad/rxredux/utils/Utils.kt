package com.zeyad.rxredux.utils

import android.content.Context
import android.os.Build
import com.zeyad.rxredux.core.Either

fun hasLollipop(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP

fun hasM(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

fun Either<String, Int>.getErrorMessage(context: Context): String {
    return when (this) {
        is Either.Left<String> -> value
        is Either.Right<Int> -> context.getString(value)
    }
}