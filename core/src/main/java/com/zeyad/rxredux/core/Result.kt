package com.zeyad.rxredux.core

import android.support.v4.util.Pair
import io.reactivex.annotations.NonNull

/**
 * @author ZIaDo on 2/27/18.
 */
open class Result<B>(val throwable: Throwable?, val eventBundlePair: Pair<String, B>,
                     val isLoading: Boolean, val isSuccessful: Boolean) {

    fun getBundle(): B {
        return eventBundlePair.second!!
    }

    @NonNull
    fun getEvent(): String? {
        return eventBundlePair.first
    }

    companion object {
        fun <B> loadingResult(): Result<B> =
                Result<B>(null, Pair.create("", null), true, false)

        fun <B> throwableResult(error: Throwable): Result<B> =
                Result<B>(error, Pair.create("", null), false, false)

        fun <B> successResult(eventBundlePair: Pair<String, B>): Result<B> =
                Result(null, eventBundlePair, false, true)
    }
}