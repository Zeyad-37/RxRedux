package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class BaseFragment<I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> : androidx.fragment.app.Fragment(), BaseView<I, R, S, E, VM> {

    override var intentStream: Observable<I> = Observable.empty()
    override lateinit var disposable: Disposable
    override lateinit var viewModel: VM
    override lateinit var viewState: S

    /**
     * returns if the viewState has been initialized
     */
    override fun isViewStateInitialized(): Boolean = ::viewState.isInitialized

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewState(savedInstanceState)
        initialize()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activate()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        onSaveInstanceStateImpl(outState, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
    }

    override fun onResume() {
        super.onResume()
        connectIntentStreamToVM()
    }

    override fun onPause() {
        disposeIntentStream()
        super.onPause()
    }
}
