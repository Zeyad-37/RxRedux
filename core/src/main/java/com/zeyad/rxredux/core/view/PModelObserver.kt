package com.zeyad.rxredux.core.view

import android.os.Parcelable
import androidx.lifecycle.Observer
import com.zeyad.rxredux.core.*

class PModelObserver<I, V : BaseView<I, S, E>, S : Parcelable, E>(private val view: V) : Observer<PModel<*, I>> {
    override fun onChanged(uiModel: PModel<*, I>?) {
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
