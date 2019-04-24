package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class BaseFragment<R, S : Parcelable, E, VM : IBaseViewModel<R, S, E>> : Fragment(), BaseView<S, E> {

    override val postOnResumeEvents = PublishSubject.create<BaseEvent<*>>()
    override var eventObservable: Observable<BaseEvent<*>> = Observable.empty()
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
        vmStart(viewModel, viewState!!, events(), this, this)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    override fun events(): Observable<BaseEvent<*>> = eventObservable.mergeWith(postOnResumeEvents)
}
