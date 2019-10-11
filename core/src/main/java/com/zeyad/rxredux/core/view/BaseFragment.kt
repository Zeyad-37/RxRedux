package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import com.uber.autodispose.AutoDispose.autoDisposable
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class BaseFragment<I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> : androidx.fragment.app.Fragment(), BaseView<I, S, E> {

    override var eventObservable: Observable<I> = Observable.empty()
    override lateinit var disposable: Disposable
    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewState?.let { vmStart(viewModel, it, this, this) }
                ?: run { throw IllegalArgumentException("ViewState is not initialized") }
        eventObservable.`as`(autoDisposable(AndroidLifecycleScopeProvider.from(this)))
                .subscribe { viewModel.offer(it) }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        onSaveInstanceStateImpl(outState, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
    }

//    override fun onResume() {
//        super.onResume()
//
//    }

//    override fun onPause() {
//        onPauseImpl()
//        super.onPause()
//    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
