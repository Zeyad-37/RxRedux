package com.zeyad.rxredux.core.eventbus;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.PublishSubject;

/**
 * Small wrapper on top of the EventBus to allow consumption of events as Rx streams.
 *
 * @author Zeyad
 */
final class RxEventBus implements IRxEventBus {

    private static IRxEventBus mInstance;
    @NonNull
    private final PublishSubject<Object> rxBus;
    private final BackpressureStrategy backpressureStrategy;

    private RxEventBus() {
        rxBus = PublishSubject.create();
        backpressureStrategy = BackpressureStrategy.BUFFER;
    }

    private RxEventBus(BackpressureStrategy backpressureStrategy) {
        rxBus = PublishSubject.create();
        this.backpressureStrategy = backpressureStrategy;
    }

    static IRxEventBus getInstance() {
        if (mInstance == null) {
            mInstance = new RxEventBus();
        }
        return mInstance;
    }

    static IRxEventBus getInstance(BackpressureStrategy backpressureStrategy) {
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
        return rxBus.toFlowable(backpressureStrategy);
    }

    @Override
    public boolean hasFlowables() {
        return rxBus.hasObservers();
    }
}

