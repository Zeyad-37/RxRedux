package com.zeyad.rxredux.core

import io.reactivex.annotations.NonNull

/**
 * @author ZIaDo on 2/27/18.
 */
/** Interface representing a View that will use to load data.  */
interface LoadDataView<S> {
    /**
     * Renders the model of the view
     *
     * @param successState the model to be rendered.
     */
    fun renderSuccessState(@NonNull successState: S, event: String)

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
    fun showError(@NonNull errorMessage: String)

    /**
     * Sets the viewState on the implementing View.
     * @param bundle state to be saved.
     */
    fun setState(bundle: S)
}
