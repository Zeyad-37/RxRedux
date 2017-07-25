package com.zeyad.rxredux.core.eventbus;

import io.reactivex.BackpressureStrategy;

public final class RxEventBusFactory {

    private RxEventBusFactory() {
    }

    public static IRxEventBus getInstance() {
        return RxEventBus.getInstance();
    }

    public static IRxEventBus getInstance(BackpressureStrategy backpressureStrategy) {
        return RxEventBus.getInstance(backpressureStrategy);
    }
}
