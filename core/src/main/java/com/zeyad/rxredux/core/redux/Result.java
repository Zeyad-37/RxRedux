package com.zeyad.rxredux.core.redux;

/**
 * @author by ZIaDo on 4/19/17.
 */
final class Result<B> {

    private final boolean isLoading, isSuccessful;
    private final Throwable error;
    private final ResultBundle<?, B> bundle;

    private Result(boolean isLoading, Throwable error, boolean isSuccessful, ResultBundle<?, B> bundle) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccessful = isSuccessful;
        this.bundle = bundle;
    }

    static <B> Result<B> loadingResult() {
        return new Result<>(true, null, false, new ResultBundle<BaseEvent, B>("", null));
    }

    static <B> Result<B> errorResult(Throwable error) {
        return new Result<>(false, error, false, new ResultBundle<BaseEvent, B>("", null));
    }

    static <B> Result<B> successResult(ResultBundle<?, B> bundle) {
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

    String getEvent() {
        return bundle == null ? "" : bundle.getEvent();
    }
}
