package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import io.reactivex.Observable

abstract class BaseFragment<S : Parcelable, VM : BaseViewModel<S>> : Fragment(), LoadDataView<S> {

    lateinit var viewModel: VM
    lateinit var viewState: S

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
    }

    override fun onStart() {
        super.onStart()
        if (!::viewState.isInitialized) {
            viewState = initialState()
        }
        vmStart(viewModel, viewState, events(), errorMessageFactory(), this, this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(UI_MODEL, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    abstract fun errorMessageFactory(): ErrorMessageFactory

    /**
     * Initialize any objects or any required dependencies.
     */
    abstract fun initialize()

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
