package com.zeyad.rxredux.core

import android.support.v4.util.Pair
import io.reactivex.annotations.NonNull

/**
 * @author ZIaDo on 2/27/18.
 */
class UIModel<S>(val stateName: String, throwable: Throwable?, eventBundlePair: Pair<String, S>,
                 isLoading: Boolean, isSuccessful: Boolean) :
        Result<S>(throwable, eventBundlePair, isLoading, isSuccessful) {

    private fun getKeySelector(): String =
            if (stateName.equals(LOADING, true)) {
                stateName
            } else {
                stateName + eventBundlePair.toString()
            }

    override fun toString(): String = String.format("State: %s, event: %s,Bundle type: %s, Error: %s, Key Selector: %s",
            stateName, eventBundlePair.first,
//            if (getBundle() != null) getBundle()::class.javaClass.simpleName else "null",
            if (getBundle() != null) "" else "null",
            if (throwable != null) throwable.message else "null",
            getKeySelector())

    companion object {
        private const val LOADING = "loading"
        private const val ERROR = "throwable"
        private const val SUCCESS = "success"
        internal const val IDLE = "idle"

        @NonNull
        fun <S> idleState(eventBundlePair: Pair<String, S>): UIModel<S> =
                UIModel(IDLE, null, eventBundlePair, false, false)

        @NonNull
        internal fun <S> loadingState(eventBundlePair: Pair<String, S>): UIModel<S> =
                UIModel(LOADING, null, eventBundlePair, true, false)

        @NonNull
        internal fun <S> errorState(error: Throwable?, eventBundlePair: Pair<String, S>): UIModel<S> =
                UIModel(ERROR, error, eventBundlePair, false, false)


        @NonNull
        internal fun <S> successState(eventBundlePair: Pair<String, S>): UIModel<S> =
                UIModel(SUCCESS, null, eventBundlePair, false, true)
    }
}