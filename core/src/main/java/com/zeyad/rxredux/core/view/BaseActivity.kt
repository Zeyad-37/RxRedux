package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.zeyad.rxredux.core.viewmodel.BaseViewModel

abstract class BaseActivity<S : Parcelable, VM : BaseViewModel<S>> : AppCompatActivity(), BaseView<S> {

    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
        setupUI(savedInstanceState == null)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        onSaveInstanceState(bundle, viewState)
        super.onSaveInstanceState(bundle)
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

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
