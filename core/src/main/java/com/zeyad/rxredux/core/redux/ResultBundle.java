package com.zeyad.rxredux.core.redux;

/**
 * @author by ZIaDo on 6/3/17.
 */
class ResultBundle<E extends BaseEvent, B> {

    private final String event;
    private final B bundle;

    ResultBundle(E event, B bundle) {
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
