package com.zeyad.rxredux.core

import android.arch.lifecycle.Observer
import android.util.Log

/**
 * @author ZIaDo on 2/27/18.
 */
class UIObserver<V : LoadDataView<S>, S>(private val view: V, private val errorMessageFactory: ErrorMessageFactory) :
        Observer<UIModel<S>> {
    override fun onChanged(uiModel: UIModel<S>?) {
        Log.d("onNext", "UIModel: " + uiModel.toString())
        val loading = uiModel?.isLoading
        view.toggleViews(loading!!)
        if (loading) {
            if (uiModel.isSuccessful) {
                val bundle = uiModel.getBundle()
                view.setState(bundle!!)
                view.renderSuccessState(bundle)
            } else {
                val error = uiModel.throwable
                if (error != null) {
                    Log.e("UIObserver", "onChanged", error)
                    view.showError(errorMessageFactory.getErrorMessage(error))
                }
            }
        }
    }
}