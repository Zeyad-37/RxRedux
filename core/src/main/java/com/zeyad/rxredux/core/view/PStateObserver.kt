package com.zeyad.rxredux.core.view

import android.arch.lifecycle.Observer
import android.util.Log
import com.zeyad.rxredux.core.*

class PStateObserver<V : BaseView<S, E>, S, E>(private val view: V) : Observer<PModel<*>> {
    override fun onChanged(uiModel: PModel<*>?) {
        Log.d("IBaseViewModel", "PStateObserver -> PModel: $uiModel")
        uiModel?.apply {
            view.toggleViews(this is LoadingEffect, event)
            when (this) {
                is ErrorEffect -> view.showError(errorMessage, event)
                is SuccessEffect -> view.applyEffect(bundle as E)
                is SuccessState -> {
                    val successStateBundle = bundle as S
                    view.setState(successStateBundle)
                    view.renderSuccessState(successStateBundle)
                }
            }
        }
    }
}
