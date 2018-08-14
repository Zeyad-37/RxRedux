package com.zeyad.rxredux.core.view

import android.arch.lifecycle.Observer
import android.util.Log
import com.zeyad.rxredux.core.ErrorState
import com.zeyad.rxredux.core.SuccessState
import com.zeyad.rxredux.core.UIModel

/**
 * @author Zeyad Gasser.
 */
class UIObserver<V : LoadDataView<S>, S>(private val view: V, private val errorMessageFactory: ErrorMessageFactory) :
        Observer<UIModel<S>> {
    override fun onChanged(uiModel: UIModel<S>?) {
        Log.d("onNext", "UIModel: " + uiModel.toString())
        val loading = uiModel?.isLoading!!
        val event = uiModel.event
        view.toggleViews(loading, event)
        if (!loading) {
            when (uiModel) {
                is ErrorState -> {
                    val error = uiModel.error
                    Log.e("UIObserver", "onChanged", error)
                    view.showError(errorMessageFactory.getErrorMessage(error, event), event)
                }
                is SuccessState -> {
                    val bundle = uiModel.bundle
                    view.setState(bundle)
                    view.renderSuccessState(bundle, event)
                }
            }
        }
    }
}