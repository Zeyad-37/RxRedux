package com.zeyad.rxredux.core.redux;

import android.support.v4.util.Pair;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

/**
 * @author by Zeyad.
 */
// TODO: 1/8/18 Make Sealed Classes in Kotlin!
public class Result<B> {

    final Throwable throwable;
    final Pair<String, B> eventBundlePair;
    private final boolean isLoading;
    private final boolean isSuccessful;

    Result(boolean isLoading, Throwable throwable, boolean isSuccessful, Pair<String, B> eventBundlePair) {
        this.isLoading = isLoading;
        this.throwable = throwable;
        this.isSuccessful = isSuccessful;
        this.eventBundlePair = eventBundlePair;
    }

    @Nullable
    static <B> Result<B> loadingResult() {
        return new Result<>(true, null, false, Pair.<String, B>create("", null));
    }

    @Nullable
    static <B> Result<B> throwableResult(Throwable error) {
        return new Result<>(false, error, false, Pair.<String, B>create("", null));
    }

    @NonNull
    static <B> Result<B> successResult(Pair<String, B> eventBundlePair) {
        return new Result<>(false, null, true, eventBundlePair);
    }

    public boolean isLoading() {
        return isLoading;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }

    public B getBundle() {
        return eventBundlePair.second;
    }

    @NonNull
    public String getEvent() {
        return eventBundlePair.first;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result<?> result = (Result<?>) o;

        if (isLoading != result.isLoading) return false;
        if (isSuccessful != result.isSuccessful) return false;
        if (throwable != null ? !throwable.equals(result.throwable) : result.throwable != null)
            return false;
        return eventBundlePair.equals(result.eventBundlePair);
    }

    @Override
    public int hashCode() {
        int result = throwable != null ? throwable.hashCode() : 0;
        result = 31 * result + eventBundlePair.hashCode();
        result = 31 * result + (isLoading ? 1 : 0);
        result = 31 * result + (isSuccessful ? 1 : 0);
        return result;
    }
}
