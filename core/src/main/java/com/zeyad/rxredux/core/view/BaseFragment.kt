package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class BaseFragment<I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> : androidx.fragment.app.Fragment(), BaseView<I, S, E> {

    override val postOnResumeEvents = PublishSubject.create<I>()
    override var eventObservable: Observable<I> = Observable.empty()
    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        onSaveInstanceStateImpl(outState, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
    }

    override fun onStart() {
        super.onStart()
        //TODO considered move this call to onCreate() to bind the events only once.
        vmStart(viewModel, viewState!!, events(), this, this)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
