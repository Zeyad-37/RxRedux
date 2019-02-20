package com.zeyad.rxredux.core.view

import android.arch.lifecycle.Observer
import com.zeyad.rxredux.core.ErrorState
import com.zeyad.rxredux.core.LoadingState
import com.zeyad.rxredux.core.PState
import com.zeyad.rxredux.core.SuccessState

class PStateObserver<V : BaseView<S>, S>(private val view: V) : Observer<PState<S>> {
    override fun onChanged(uiModel: PState<S>?) {
        uiModel?.apply {
            view.toggleViews(this is LoadingState, event)
            when (this) {
                is ErrorState -> view.showError(errorMessage, event)
                is SuccessState -> {
                    view.setState(bundle)
                    view.renderSuccessState(bundle)
                }
            }
        }
    }
}
