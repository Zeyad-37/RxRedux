package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import android.view.View
import com.zeyad.rxredux.core.viewmodel.BaseViewModel

abstract class BaseFragment<S : Parcelable, VM : BaseViewModel<S>> : Fragment(), BaseView<S> {

    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI(savedInstanceState == null)
    }

    override fun onStart() {
        super.onStart()
        if (viewState == null) {
            viewState = initialState()
        }
        viewState?.let {
            vmStart(viewModel, it, events(), errorMessageFactory(), this, this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(P_MODEL, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
