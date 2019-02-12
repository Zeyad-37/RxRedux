package com.zeyad.rxredux.core.view

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable
import org.reactivestreams.Publisher

const val P_MODEL = "viewState"

fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?): S? =
        if (savedInstanceState != null && savedInstanceState.containsKey(P_MODEL))
            savedInstanceState.getParcelable(P_MODEL)
        else null

fun <S : Parcelable> onSaveInstanceState(bundle: Bundle, viewState: S?) =
        bundle.putParcelable(P_MODEL, viewState)

fun <T> Publisher<T>.toLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this)

fun <S : Parcelable, VM : IBaseViewModel<S>> vmStart(viewModel: VM?, viewState: S,
                                                     events: Observable<BaseEvent<*>>,
                                                     errorMessageFactory: ErrorMessageFactory,
                                                     view: BaseView<S>,
                                                     lifecycleOwner: LifecycleOwner) {
    viewModel?.store(events, viewState)?.toLiveData()
            ?.observe(lifecycleOwner, PModObserver(view, errorMessageFactory))
}

interface IBaseView<S : Parcelable, VM : IBaseViewModel<S>> : BaseView<S>, LifecycleOwner {
    var viewModel: VM?
    var viewState: S?

    fun onSaveInstanceStateImpl(bundle: Bundle) = onSaveInstanceState(bundle, viewState)

    fun onStartImpl() {
        if (viewState == null) {
            viewState = initialState()
        }
        viewState?.let { vmStart(viewModel, it, events(), errorMessageFactory(), this, this) }
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    /**
     * @return initial state of view
     */
    fun initialState(): S
}
