package com.zeyad.rxredux.core.eventbus;

import android.support.annotation.NonNull;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.PublishSubject;

/**
 * Small wrapper on top of the EventBus to allow consumption of events as Rx streams.
 *
 * @author Zeyad
 */
final class RxEventBus implements IRxEventBus {

    private static IRxEventBus mInstance;
    private final PublishSubject<Object> rxBus;

    private RxEventBus() {
        rxBus = PublishSubject.create();
    }

    static IRxEventBus getInstance() {
        if (mInstance == null) {
            mInstance = new RxEventBus();
        }
        return mInstance;
    }

    @Override
    public void send(Object o) {
        rxBus.onNext(o);
    }

    @Override
    @NonNull
    public Flowable<Object> toFlowable() {
        return rxBus.toFlowable(BackpressureStrategy.BUFFER);
    }

    @Override
    public boolean hasFlowables() {
        return rxBus.hasObservers();
    }
}

