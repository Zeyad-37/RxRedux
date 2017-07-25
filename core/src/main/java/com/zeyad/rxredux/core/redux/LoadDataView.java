package com.zeyad.rxredux.core.redux;

import io.reactivex.annotations.NonNull;

/** Interface representing a View that will use to load data. */
interface LoadDataView<S> {
    /**
     * Renders the model of the view
     *
     * @param successState the model to be rendered.
     */
    void renderSuccessState(@NonNull S successState);

    /**
     * Show or hide a view with a progress bar indicating a loading process.
     *
     * @param isLoading whether to show or hide the loading view.
     */
    void toggleViews(boolean isLoading);

    /**
     * Show an errorResult message
     *
     * @param errorMessage A string representing an errorResult.
     */
    void showError(@NonNull String errorMessage);
}
