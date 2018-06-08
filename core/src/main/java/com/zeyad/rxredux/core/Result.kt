package com.zeyad.rxredux.core

import android.support.v4.util.Pair

/**
 * @author ZIaDo on 2/27/18.
 */
open class Result<B>(val throwable: Throwable?, val eventBundlePair: Pair<String, B>,
                     val isLoading: Boolean, val isSuccessful: Boolean) {

    fun getBundle(): B {
        return eventBundlePair.second!!
    }

    fun getEvent(): String? {
        return eventBundlePair.first
    }

    companion object {
        fun loadingResult() = Result(null, Pair("", Any()), true, false)

        fun throwableResult(error: Throwable) = Result(error, Pair("", Any()), false, false)

        fun <B> successResult(eventBundlePair: Pair<String, B>): Result<B> =
                Result(null, eventBundlePair, false, true)
    }
}