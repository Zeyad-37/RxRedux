package com.zeyad.rxredux.core.view

import android.os.Parcelable
import androidx.lifecycle.LifecycleOwner
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable

fun <I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> vmStart(viewModel: VM, initialState: S,
                                                                       events: Observable<I>,
                                                                       view: BaseView<I, S, E>,
                                                                       lifecycleOwner: LifecycleOwner) =
        viewModel.store(events, initialState).observe(lifecycleOwner, PModelObserver(view))

interface IBaseView<I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> : BaseView<I, S, E>, LifecycleOwner {
    var viewModel: VM?
    var viewState: S?

    fun onStartImpl() {
        viewModel?.let { vm ->
            viewState?.let { vs ->
                vm.disposables.clear()
                vmStart(vm, vs, events(), this, this)
            } ?: run { throw KotlinNullPointerException("ViewState is null!") }
        } ?: run {
            throw KotlinNullPointerException("ViewModel is null!")
        }
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
