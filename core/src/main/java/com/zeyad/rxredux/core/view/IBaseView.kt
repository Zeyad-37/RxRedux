package com.zeyad.rxredux.core.view

import android.os.Parcelable
import androidx.lifecycle.LifecycleOwner
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel

fun <I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> vmStart(viewModel: VM, initialState: S,
                                                                       view: BaseView<I, S, E>,
                                                                       lifecycleOwner: LifecycleOwner) =
        viewModel.store(initialState).observe(lifecycleOwner, PModelObserver(view))

interface IBaseView<I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> : BaseView<I, S, E>, LifecycleOwner {
    var viewModel: VM?
    var viewState: S?

    fun onStartImpl() {
        viewModel?.let { vm: VM ->
            viewState?.let { vs: S ->
                vmStart(vm, vs, this, this)
            } ?: throw KotlinNullPointerException("ViewState is null!")
        } ?: throw KotlinNullPointerException("ViewModel is null!")
    }

    fun connectIntentStreamToVM() {
        disposable = intentStream.subscribe { viewModel?.offer(it) }
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
