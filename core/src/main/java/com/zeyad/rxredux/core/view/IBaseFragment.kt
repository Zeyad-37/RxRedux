package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel

interface IBaseFragment<R, S : Parcelable, E, VM : IBaseViewModel<R, S, E>> : IBaseView<R, S, E, VM> {

    fun onCreateImpl(savedInstanceState: Bundle?) {
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
    }

    fun onViewStateRestoredImpl(savedInstanceState: Bundle?) {
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
    }
}
