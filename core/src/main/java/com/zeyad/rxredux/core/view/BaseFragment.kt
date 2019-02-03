package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.zeyad.rxredux.core.viewmodel.BaseViewModel

abstract class BaseFragment<S : Parcelable, VM : BaseViewModel<S>> : Fragment(), IBaseView<S> {

    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
    }

    override fun onStart() {
        super.onStart()
        if (viewState == null) {
            viewState = initialState()
            vmStart(viewModel, initialState(), events(), errorMessageFactory(), this, this)
        } else {
            vmStart(viewModel, viewState!!, events(), errorMessageFactory(), this, this)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(P_MODEL, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    /**
     * @return initial state of view
     */
    abstract fun initialState(): S
}
