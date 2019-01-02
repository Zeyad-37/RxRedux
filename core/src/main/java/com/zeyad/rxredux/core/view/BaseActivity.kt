package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import io.reactivex.Observable

abstract class BaseActivity<S : Parcelable, VM : BaseViewModel<S>> : AppCompatActivity(), LoadDataView<S> {
    lateinit var viewModel: VM
    lateinit var viewState: S

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
        if (!::viewState.isInitialized) {
            viewState = initialState()
        }
        vmStart(viewModel, viewState, events(), errorMessageFactory(), this, this)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    abstract fun errorMessageFactory(): ErrorMessageFactory

    /**
     * Initialize objects or any required dependencies.
     */
    abstract fun initialize()

    /**
     * Setup the UI.
     *
     * @param isNew = savedInstanceState == null
     */
    abstract fun setupUI(isNew: Boolean)

    /**
     * Merge all events into one [Observable].
     *
     * @return [Observable].
     */
    abstract fun events(): Observable<BaseEvent<*>>

    /**
     * @return initial state of view
     */
    abstract fun initialState(): S
}
