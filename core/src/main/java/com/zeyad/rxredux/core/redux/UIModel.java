package com.zeyad.rxredux.core.redux;

import android.support.v4.util.Pair;

import io.reactivex.annotations.NonNull;

/**
 * @author Zeyad.
 */
public final class UIModel<S> extends Result<S> {
    static final String IDLE = "idle";
    private static final String LOADING = "loading", ERROR = "throwable", SUCCESS = "success";
    private final String stateName;

    private UIModel(String stateName, boolean isLoading, Throwable error, boolean isSuccessful,
                    Pair<String, S> eventBundlePair) {
        super(isLoading, error, isSuccessful, eventBundlePair);
        this.stateName = stateName;
    }

    @NonNull
    static <S> UIModel<S> idleState(Pair<String, S> eventBundlePair) {
        return new UIModel<>(IDLE, false, null, false, eventBundlePair);
    }

    @NonNull
    static <S> UIModel<S> loadingState(Pair<String, S> eventBundlePair) {
        return new UIModel<>(LOADING, true, null, false, eventBundlePair);
    }

    @NonNull
    static <S> UIModel<S> errorState(Throwable error, Pair<String, S> eventBundlePair) {
        return new UIModel<>(ERROR, false, error, false, eventBundlePair);
    }

    @NonNull
    static <S> UIModel<S> successState(Pair<String, S> eventBundlePair) {
        return new UIModel<>(SUCCESS, false, null, true, eventBundlePair);
    }

    @NonNull
    private String getKeySelector() {
        if (stateName.equalsIgnoreCase(LOADING)) {
            return stateName;
        } else {
            if (eventBundlePair != null) return stateName + eventBundlePair.toString();
            else return stateName + "";
        }
    }

    @Override
    public String toString() {
        return String.format("State: %s, event: %s,Bundle type: %s, Error: %s, Key Selector: %s",
                stateName, String.valueOf(eventBundlePair.first),
                (getBundle() != null ? getBundle().getClass().getSimpleName() : "null"),
                String.valueOf(throwable.getLocalizedMessage()),
//                (throwable != null ? throwable.getMessage() : "null"),
                getKeySelector());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        UIModel<?> uiModel = (UIModel<?>) o;

        return stateName.equals(uiModel.stateName);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + stateName.hashCode();
        return result;
    }
}
