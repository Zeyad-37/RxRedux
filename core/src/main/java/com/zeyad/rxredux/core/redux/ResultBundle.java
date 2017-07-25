package com.zeyad.rxredux.core.redux;

import io.reactivex.annotations.NonNull;

/**
 * @author by Zeyad.
 */
class ResultBundle<B> {

    private final String event;
    private final B bundle;

    ResultBundle(@NonNull BaseEvent event, B bundle) {
        this.event = event.getClass().getSimpleName();
        this.bundle = bundle;
    }

    ResultBundle(String event, B bundle) {
        this.event = event;
        this.bundle = bundle;
    }

    String getEvent() {
        return event;
    }

    B getBundle() {
        return bundle;
    }

    @Override
    public String toString() {
        return String.format("Event: %s, bundle %s", event, (bundle != null ? bundle.toString() : ""));
    }
}
