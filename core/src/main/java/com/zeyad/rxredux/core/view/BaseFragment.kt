package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.zeyad.rxredux.core.viewmodel.BaseViewModel

abstract class BaseFragment<R, S : Parcelable, E, VM : BaseViewModel<R, S, E>> : Fragment(), BaseView<S, E> {

    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
    }

    override fun onStart() {
        super.onStart()
        vmStart(viewModel, viewState!!, events(), this, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(P_MODEL, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
