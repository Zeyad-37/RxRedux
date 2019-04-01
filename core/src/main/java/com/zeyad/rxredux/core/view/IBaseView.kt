package com.zeyad.rxredux.core.view

import android.arch.lifecycle.LifecycleOwner
import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable

fun <R, S : Parcelable, E, VM : IBaseViewModel<R, S, E>> vmStart(viewModel: VM, initialState: S,
                                                                                   events: Observable<BaseEvent<*>>,
                                                                                   view: BaseView<S, E>,
                                                                                   lifecycleOwner: LifecycleOwner) =
        viewModel.store(events, initialState).observe(lifecycleOwner, PModelObserver(view))

interface IBaseView<R, S : Parcelable, E, VM : IBaseViewModel<R, S, E>> : BaseView<S, E>, LifecycleOwner {
    var viewModel: VM?
    var viewState: S?

    fun onStartImpl() {
        vmStart(viewModel!!, viewState!!, events(), this, this)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
