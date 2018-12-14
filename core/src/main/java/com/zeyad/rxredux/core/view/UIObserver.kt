package com.zeyad.rxredux.core.view

import android.arch.lifecycle.Observer
import com.zeyad.rxredux.core.ErrorState
import com.zeyad.rxredux.core.SuccessState
import com.zeyad.rxredux.core.UIModel

class UIObserver<V : LoadDataView<S>, S>(private val view: V,
                                         private val errorMessageFactory: ErrorMessageFactory
) : Observer<UIModel<S>> {
    override fun onChanged(uiModel: UIModel<S>?) {
        uiModel?.apply {
            view.toggleViews(isLoading, event)
            if (!isLoading) {
                when (this) {
                    is ErrorState ->
                        view.showError(errorMessageFactory.invoke(error, event), event)
                    is SuccessState -> {
                        view.setState(bundle)
                        view.renderSuccessState(bundle)
                    }
                }
            }
        }
    }
}
