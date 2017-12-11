package com.zeyad.rxredux.core.eventbus;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;

public interface IRxEventBus<T> {
    void send(@NonNull T t);

//    @NonNull
//    Flowable<Object> toFlowable();

    @NonNull
    Flowable<T> toFlowable();

    boolean hasFlowables();
}
