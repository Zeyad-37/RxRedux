package com.zeyad.rxredux.core.redux;

/**
 * @author zeyad on 1/24/17.
 */
final class UIModel<S> {
    static final String LOADING = "loading", ERROR = "error", SUCCESS = "success";
    private static final String IDLE = "idle";
    private final boolean isLoading, isSuccessful;
    private final Throwable error;
    private final String state;
    private final ResultBundle<?, S> bundle;

    private UIModel(String state, boolean isLoading, Throwable error, boolean isSuccessful,
                    ResultBundle<?, S> bundle) {
        this.isLoading = isLoading;
        this.error = error;
        this.isSuccessful = isSuccessful;
        this.bundle = bundle;
        this.state = state;
    }

    static <S> UIModel<S> idleState(ResultBundle<?, S> bundle) {
        return new UIModel<>(IDLE, false, null, false, bundle);
    }

    static <S> UIModel<S> loadingState(ResultBundle<?, S> bundle) {
        return new UIModel<>(LOADING, true, null, false, bundle);
    }

    static <S> UIModel<S> errorState(Throwable error, ResultBundle<?, S> bundle) {
        return new UIModel<>(ERROR, false, error, false, bundle);
    }

    static <S> UIModel<S> successState(ResultBundle<?, S> bundle) {
        return new UIModel<>(SUCCESS, false, null, true, bundle);
    }

    boolean isLoading() {
        return isLoading;
    }

    Throwable getError() {
        return error;
    }

    S getBundle() {
        return bundle.getBundle();
    }

    ResultBundle<?, S> getResultBundle() {
        return bundle;
    }

    boolean isSuccessful() {
        return isSuccessful;
    }

    @Override
    public String toString() {
        return String.format("State: %s, Error: %s, Bundle type: %s, Key Selector: %s",
                (state.equalsIgnoreCase(SUCCESS) ? state + ", event: " + bundle.getEvent() : state),
                (error != null ? error.getMessage() : "null"),
                (getBundle() != null ? getBundle().getClass().getSimpleName() : "null"),
                state.equalsIgnoreCase(LOADING) ? state : state + (bundle != null ?
                        bundle.toString() : ""));
    }
}
