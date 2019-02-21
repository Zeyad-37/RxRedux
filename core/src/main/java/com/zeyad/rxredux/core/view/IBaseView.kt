package com.zeyad.rxredux.core.view

import android.arch.lifecycle.LifecycleOwner
import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable

const val P_MODEL = "viewState"

fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?): S? =
        if (savedInstanceState != null && savedInstanceState.containsKey(P_MODEL))
            savedInstanceState.getParcelable(P_MODEL)
        else null

fun <S : Parcelable> onSaveInstanceState(bundle: Bundle, viewState: S?) =
        bundle.putParcelable(P_MODEL, viewState)

fun <S : Parcelable, VM : IBaseViewModel<S>> vmStart(viewModel: VM, initialState: S,
                                                     events: Observable<BaseEvent<*>>,
                                                     view: BaseView<S>,
                                                     lifecycleOwner: LifecycleOwner) {
    val (states, effects) = viewModel.store(events, initialState)
    states.observe(lifecycleOwner, PStateObserver(view))
    effects.observe(lifecycleOwner, PEffectObserver(view))
}

interface IBaseView<S : Parcelable, VM : IBaseViewModel<S>> : BaseView<S>, LifecycleOwner {
    var viewModel: VM?
    var viewState: S?

    fun onSaveInstanceStateImpl(bundle: Bundle) = onSaveInstanceState(bundle, viewState)

    fun onStartImpl() {
        vmStart(viewModel!!, viewState!!, events(), this, this)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
