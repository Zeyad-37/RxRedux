package com.zeyad.rxredux.core

import io.reactivex.annotations.NonNull

/**
 * @author ZIaDo on 2/27/18.
 */
interface StateReducer<S> {
    @NonNull
    fun reduce(@NonNull newResult: Any?, @NonNull event: String?, @NonNull currentStateBundle: S?): S
}