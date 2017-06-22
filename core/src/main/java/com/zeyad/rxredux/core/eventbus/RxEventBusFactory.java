package com.zeyad.rxredux.core.eventbus;

public final class RxEventBusFactory {

    private RxEventBusFactory() {
    }

    public static IRxEventBus getInstance() {
        return RxEventBus.getInstance();
    }
}
