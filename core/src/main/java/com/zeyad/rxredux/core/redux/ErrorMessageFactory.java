package com.zeyad.rxredux.core.redux;

import io.reactivex.annotations.NonNull;

/** @author by Zeyad. */
public interface ErrorMessageFactory {
    @NonNull
    String getErrorMessage(Throwable throwable);
}
