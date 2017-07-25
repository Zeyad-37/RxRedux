package com.zeyad.rxredux.core.redux;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * @author by Zeyad.
 */
class Result<B> {

    final Throwable error;
    final ResultBundle<B> bundle;
    private final boolean isLoading, isSuccessful;

    Result(boolean isLoading, Throwable error, boolean isSuccessful, ResultBundle<B> bundle) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccessful = isSuccessful;
        this.bundle = bundle;
    }

    @Nullable
    static <B> Result<B> loadingResult() {
        return new Result<>(true, null, false, new ResultBundle<B>("", null));
    }

    @Nullable
    static <B> Result<B> errorResult(Throwable error) {
        return new Result<>(false, error, false, new ResultBundle<B>("", null));
    }

    @NonNull
    static <B> Result<B> successResult(ResultBundle<B> bundle) {
        return new Result<>(false, null, true, bundle);
    }

    boolean isLoading() {
        return isLoading;
    }

    Throwable getError() {
        return error;
    }

    boolean isSuccessful() {
        return isSuccessful;
    }

    B getBundle() {
        B bundle = this.bundle.getBundle();
        return bundle != null ? bundle : (B) new Object();
    }

    @NonNull
    String getEvent() {
        return bundle == null ? "" : bundle.getEvent();
    }
}
