package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

const val P_MODEL = "viewState"

fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?): S? =
        if (savedInstanceState != null && savedInstanceState.containsKey(P_MODEL))
            savedInstanceState.getParcelable(P_MODEL)
        else null

interface BaseView<I, S : Parcelable, E> {

    var intentStream: Observable<I>

    var disposable: Disposable

    fun <S : Parcelable> onSaveInstanceStateImpl(bundle: Bundle, viewState: S?) =
            bundle.putParcelable(P_MODEL, viewState)

    fun disposeIntentStream() {
        if (!disposable.isDisposed) {
            disposable.dispose()
        }
    }

    /**
     * Initialize objects or any required dependencies.
     */
    fun initialize()

    /**
     * Renders the model of the view
     *
     * @param successState the model to be rendered.
     */
    fun bindState(successState: S)

    /**
     * Apply the effect to the view
     *
     * @param effectBundle the model to be rendered.
     */
    fun bindEffect(effectBundle: E)

    /**
     * Show or hide a view with a progress bar indicating a loading process.
     *
     * @param isLoading whether to show or hide the loading view.
     */
    fun toggleLoadingViews(isLoading: Boolean, intent: I?)

    /**
     * Show an errorResult messageId
     *
     * @param errorMessage A string representing an errorResult.
     */
    fun bindError(errorMessage: String, cause: Throwable, intent: I)

    /**
     * Sets the viewState and the firing intent on the implementing View.
     * @param bundle state to be saved.
     */
    fun setState(bundle: S)
}
