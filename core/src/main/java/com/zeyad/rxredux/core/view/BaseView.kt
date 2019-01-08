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

const val UI_MODEL = "viewState"

fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?): S? =
        if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL))
            savedInstanceState.getParcelable(UI_MODEL)
        else null

fun <S : Parcelable> onSaveInstanceState(bundle: Bundle, viewState: S?) =
        bundle.putParcelable(UI_MODEL, viewState)

fun <T> Publisher<T>.toLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this)

fun <S : Parcelable, VM : IBaseViewModel<S>> vmStart(viewModel: VM?, viewState: S,
                                                     events: Observable<BaseEvent<*>>,
                                                     errorMessageFactory: ErrorMessageFactory,
                                                     view: LoadDataView<S>,
                                                     lifecycleOwner: LifecycleOwner) {
    viewModel?.store(events, viewState)?.toLiveData()
            ?.observe(lifecycleOwner, PModObserver(view, errorMessageFactory))
}

typealias ErrorMessageFactory = (throwable: Throwable, event: BaseEvent<*>) -> String

interface BaseView<S : Parcelable, VM : IBaseViewModel<S>> : LifecycleOwner, LoadDataView<S> {
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
}
