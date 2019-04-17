package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel

abstract class BaseActivity<R, S : Parcelable, E, VM : IBaseViewModel<R, S, E>> : AppCompatActivity(), BaseView<S, E> {

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
        onSaveInstanceStateImpl(bundle, viewState)
        super.onSaveInstanceState(bundle)
    }

    override fun onStart() {
        super.onStart()
        vmStart(viewModel, viewState!!, events(), this, this)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    /**
     * Setup the UI.
     *
     * @param isNew = savedInstanceState == null
     */
    abstract fun setupUI(isNew: Boolean)
}
