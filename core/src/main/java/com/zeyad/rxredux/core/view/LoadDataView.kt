package com.zeyad.rxredux.core.view

import com.zeyad.rxredux.core.BaseEvent

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
    fun renderSuccessState(successState: S)

    /**
     * Show or hide a view with a progress bar indicating a loading process.
     *
     * @param isLoading whether to show or hide the loading view.
     */
    fun toggleViews(isLoading: Boolean, event: BaseEvent<*>)

    /**
     * Show an errorResult message
     *
     * @param errorMessage A string representing an errorResult.
     */
    fun showError(errorMessage: String, event: BaseEvent<*>)

    /**
     * Sets the viewState and the firing event on the implementing View.
     * @param bundle state to be saved.
     */
    fun setStateWithEvent(bundle: S, event: BaseEvent<*>)
}
