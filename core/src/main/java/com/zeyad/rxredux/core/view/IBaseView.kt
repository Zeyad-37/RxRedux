package com.zeyad.rxredux.core.view

import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import com.uber.autodispose.AutoDispose.autoDisposable
import com.uber.autodispose.ScopeProvider
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable

fun <I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> vmStart(viewModel: VM, initialState: S,
                                                                       events: Observable<I>,
                                                                       view: BaseView<I, S, E>,
                                                                       lifecycleOwner: ScopeProvider) =
        viewModel.store(events, initialState)
                .doOnTerminate { Log.d("StartVM", "UnSubscribed") }
                .`as`(autoDisposable(lifecycleOwner))
                .subscribe(PModelSubscriber(view))

interface IBaseView<I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> : BaseView<I, S, E>, LifecycleOwner {
    var viewModel: VM?
    var viewState: S?

    fun onStartImpl() {
        viewModel?.let { vm ->
            viewState?.let { vs ->
                vmStart(vm, vs, events(), this, AndroidLifecycleScopeProvider.from(this))
            } ?: run { throw KotlinNullPointerException("ViewState is null!") }
        } ?: run {
            throw KotlinNullPointerException("ViewModel is null!")
        }
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
