package com.zeyad.rxredux.core.view

import android.arch.lifecycle.Observer
import com.zeyad.rxredux.core.ErrorState
import com.zeyad.rxredux.core.LoadingState
import com.zeyad.rxredux.core.PModel
import com.zeyad.rxredux.core.SuccessState

class PModObserver<V : IBaseView<S>, S>(private val view: V,
                                        private val errorMessageFactory: ErrorMessageFactory
) : Observer<PModel<S>> {
    override fun onChanged(uiModel: PModel<S>?) {
        uiModel?.apply {
            when (this) {
                is LoadingState -> view.toggleViews(true, event)
                is ErrorState -> {
                    view.toggleViews(false, event)
                    view.showError(errorMessageFactory.invoke(error, event), event)
                }
                is SuccessState -> {
                    view.toggleViews(false, event)
                    view.setState(bundle)
                    view.renderSuccessState(bundle)
                }
            }
        }
    }
}
