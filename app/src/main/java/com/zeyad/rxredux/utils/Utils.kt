package com.zeyad.rxredux.utils

import android.os.Build

/**
 * @author by ZIaDo on 10/1/16.
 */
object Utils {

    fun hasLollipop(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }

    fun hasM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun <T> runIfNotNull(t: T, execute: Execute<T>) {
        execute.run(t)
    }

    fun <T, R> runAndReturnIfNotNull(t: T, execute: ExecuteAndReturn<T, R>): R {
        return execute.run(t)
    }

    interface Execute<T> {
        fun run(activity: T)
    }

    interface ExecuteAndReturn<T, R> {
        fun run(activity: T): R
    }
}
