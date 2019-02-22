package com.zeyad.rxredux.core.view

import android.arch.lifecycle.Observer
import com.zeyad.rxredux.core.SuccessState

class PStateObserver<V : BaseView<S>, S>(private val view: V) : Observer<SuccessState<S>> {
    override fun onChanged(uiModel: SuccessState<S>?) {
        uiModel?.apply {
            view.toggleViews(false, event)
            view.setState(bundle)
            view.renderSuccessState(bundle)
        }
    }
}