package com.zeyad.rxredux.core.view

/**
 * Interface representing a View that will use to load data.
 *
 * @author Zeyad Gasser.
 */
interface LoadDataView<S> {
    /**
     * Renders the model of the view
     *
     * @param successState the model to be rendered.
     */
    fun renderSuccessState(successState: S, event: String)

    /**
     * Show or hide a view with a progress bar indicating a loading process.
     *
     * @param isLoading whether to show or hide the loading view.
     */
    fun toggleViews(isLoading: Boolean, event: String)

    /**
     * Show an errorResult message
     *
     * @param errorMessage A string representing an errorResult.
     */
    fun showError(errorMessage: String, event: String)

    /**
     * Sets the viewState on the implementing View.
     * @param bundle state to be saved.
     */
    fun setState(bundle: S)
}
