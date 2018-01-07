package com.zeyad.rxredux.core.redux;

import io.reactivex.annotations.NonNull;

/** @author by Zeyad. */
public interface StateReducer<S> {
    @NonNull
    S reduce(@NonNull Object newResult, @NonNull String event, @NonNull S currentStateBundle);
}
