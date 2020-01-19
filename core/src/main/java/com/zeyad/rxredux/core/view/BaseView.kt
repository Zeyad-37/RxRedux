package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import androidx.lifecycle.LifecycleOwner
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable

const val P_MODEL = "viewState"

fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?): S? =
        if (savedInstanceState?.containsKey(P_MODEL) == true)
            savedInstanceState.getParcelable(P_MODEL)
        else null

interface BaseView<I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> : LifecycleOwner {

    var intentStream: Observable<I>
    var viewModel: VM
    var viewState: S

    fun initialStateProvider(): S

    fun initViewState(savedInstanceState: Bundle?) {
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        if (!isViewStateInitialized()) {
            viewState = initialStateProvider()
        }
    }

    fun isViewStateInitialized(): Boolean

    /**
     * Initialize objects or any required dependencies, including viewModel.
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
    fun bindError(errorMessage: String, intent: I, cause: Throwable = Throwable())

    fun <S : Parcelable> onSaveInstanceStateImpl(bundle: Bundle, viewState: S?) =
            bundle.putParcelable(P_MODEL, viewState)

    fun activate() {
        viewModel.store(viewState, intentStream).observe(this, PModelObserver(this))
    }

    fun deactivate() {
        viewModel.onClearImpl()
    }

    fun setState(bundle: S) {
        viewState = bundle
    }
}
