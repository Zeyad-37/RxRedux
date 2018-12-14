package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.viewmodel.BaseViewModel

interface IBaseActivity<S : Parcelable, VM : BaseViewModel<S>> : BaseView<S, VM> {

    fun onCreateImpl(savedInstanceState: Bundle?) {
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
        setupUI(savedInstanceState == null)
    }

    /**
     * Setup the UI.
     *
     * @param isNew = savedInstanceState == null
     */
    fun setupUI(isNew: Boolean)

    fun onRestoreInstanceStateImpl(savedInstanceState: Bundle) =
            getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
}
