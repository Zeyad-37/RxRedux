package com.zeyad.rxredux.core.eventbus;

import android.support.annotation.NonNull;

import io.reactivex.Flowable;

public interface IRxEventBus {
    void send(Object o);

    @NonNull
    Flowable<Object> toFlowable();

    boolean hasFlowables();
}
