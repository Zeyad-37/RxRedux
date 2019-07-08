package com.zeyad.rxredux.core.view

import android.arch.lifecycle.LifecycleOwner
import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable

fun <I : BaseEvent<*>, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> vmStart(viewModel: VM, initialState: S,
                                                                                      events: Observable<I>,
                                                                                      view: BaseView<I, S, E>,
                                                                                      lifecycleOwner: LifecycleOwner) =
        viewModel.store(events, initialState).observe(lifecycleOwner, PModelObserver(view))

interface IBaseView<I : BaseEvent<*>, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> : BaseView<I, S, E>, LifecycleOwner {
    var viewModel: VM?
    var viewState: S?

    fun onStartImpl() {
        vmStart(viewModel!!, viewState!!, events(), this, this)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
