package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.Message
import io.reactivex.Observable

const val P_MODEL = "viewState"

fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?): S? =
        if (savedInstanceState != null && savedInstanceState.containsKey(P_MODEL))
            savedInstanceState.getParcelable(P_MODEL)
        else null

interface BaseView<S : Parcelable, E> {

    fun <S : Parcelable> onSaveInstanceStateImpl(bundle: Bundle, viewState: S?) =
            bundle.putParcelable(P_MODEL, viewState)

    /**
     * Initialize objects or any required dependencies.
     */
    fun initialize()

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
     * Apply the effect to the view
     *
     * @param effectBundle the model to be rendered.
     */
    fun applyEffect(effectBundle: E)

    /**
     * Show or hide a view with a progress bar indicating a loading process.
     *
     * @param isLoading whether to show or hide the loading view.
     */
    fun toggleViews(isLoading: Boolean, event: BaseEvent<*>)

    /**
     * Show an errorResult messageId
     *
     * @param errorMessage A string representing an errorResult.
     */
    fun showError(errorMessage: Message, event: BaseEvent<*>)

    /**
     * Sets the viewState and the firing event on the implementing View.
     * @param bundle state to be saved.
     */
    fun setState(bundle: S)
}
