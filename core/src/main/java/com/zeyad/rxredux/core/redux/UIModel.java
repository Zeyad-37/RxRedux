package com.zeyad.rxredux.core.redux;

import io.reactivex.annotations.NonNull;

/**
 * @author Zeyad.
 */
public final class UIModel<S> extends Result<S> {
    static final String IDLE = "idle";
    private static final String LOADING = "loading", ERROR = "error", SUCCESS = "success";
    private final String state;

    private UIModel(String state, boolean isLoading, Throwable error, boolean isSuccessful,
            ResultBundle<S> bundle) {
        super(isLoading, error, isSuccessful, bundle);
        this.state = state;
    }

    @NonNull
    static <S> UIModel<S> idleState(ResultBundle<S> bundle) {
        return new UIModel<>(IDLE, false, null, false, bundle);
    }

    @NonNull
    static <S> UIModel<S> loadingState(ResultBundle<S> bundle) {
        return new UIModel<>(LOADING, true, null, false, bundle);
    }

    @NonNull
    static <S> UIModel<S> errorState(Throwable error, ResultBundle<S> bundle) {
        return new UIModel<>(ERROR, false, error, false, bundle);
    }

    @NonNull
    static <S> UIModel<S> successState(ResultBundle<S> bundle) {
        return new UIModel<>(SUCCESS, false, null, true, bundle);
    }

    @Override
    S getBundle() {
        return bundle.getBundle();
    }

    public ResultBundle<S> getResultBundle() {
        return bundle;
    }

    @NonNull
    String getKeySelector() {
        return state.equalsIgnoreCase(LOADING) ? state : state + (bundle != null ? bundle.toString() : "");
    }

    @Override
    public String toString() {
        return String.format("State: %s, Error: %s, Bundle type: %s, Key Selector: %s",
                (state.equalsIgnoreCase(SUCCESS) ? state + ", event: " + bundle.getEvent() : state),
                (error != null ? error.getMessage() : "null"),
                (getBundle() != null ? getBundle().getClass().getSimpleName() : "null"),
                getKeySelector());
    }
}
