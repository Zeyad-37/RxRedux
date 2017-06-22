package com.zeyad.rxredux.core.redux;

/** Interface representing a View that will use to load data. */
interface LoadDataView<S> {
    /**
     * Renders the model of the view
     *
     * @param s the model to be rendered.
     */
    void renderState(S s);

    /**
     * Show or hide a view with a progress bar indicating a loading process.
     *
     * @param toggle whether to show or hide the loading view.
     */
    void toggleViews(boolean toggle);

    /**
     * Show an errorResult message
     *
     * @param message A string representing an errorResult.
     */
    void showError(String message);
}
