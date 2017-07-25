package com.zeyad.rxredux.core.redux;

import io.reactivex.annotations.NonNull;

/** @author by Zeyad. */
public interface SuccessStateAccumulator<S> {
    @NonNull
    S accumulateSuccessStates(@NonNull Object newResult, @NonNull String event, @NonNull S currentStateBundle);
}
