package com.zeyad.rxredux.core.view

import com.zeyad.rxredux.core.BaseEvent
import io.reactivex.Observable

interface BaseView<S> {

    /**
     * Initialize objects or any required dependencies.
     */
    fun initialize()

    /**
     * Setup the UI.
     *
     * @param isNew = savedInstanceState == null
     */
    fun setupUI(isNew: Boolean)

    /**
     * Merge all events into one [Observable].
     *
     * @return [Observable].
     */
    fun events(): Observable<BaseEvent<*>>

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
    fun setState(bundle: S)
}
