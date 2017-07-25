package com.zeyad.rxredux.core.eventbus;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;

public interface IRxEventBus {
    void send(@NonNull Object o);

    @NonNull
    Flowable<Object> toFlowable();

    boolean hasFlowables();
}
