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
        uiModel?.apply {
            Log.d("onNext", "UIModel: " + toString())
            view.toggleViews(isLoading, event)
            if (!isLoading) {
                when (this) {
                    is ErrorState -> {
                        Log.e("UIObserver", "onChanged", error)
                        view.showError(errorMessageFactory.getErrorMessage(error, event), event)
                    }
                    is SuccessState -> {
                        view.setStateWithEvent(bundle, event)
                        view.renderSuccessState(bundle)
                    }
                }
            }
        }
    }
}
