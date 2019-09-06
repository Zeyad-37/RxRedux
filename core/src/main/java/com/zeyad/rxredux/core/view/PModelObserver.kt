package com.zeyad.rxredux.core.view

import android.os.Parcelable
import androidx.lifecycle.Observer
import com.zeyad.rxredux.core.*
import io.reactivex.functions.Consumer

class PModelObserver<I, V : BaseView<I, S, E>, S : Parcelable, E>(private val view: V) : Observer<PModel<*, I>> {
    override fun onChanged(uiModel: PModel<*, I>?) {
        uiModel?.apply {
            when (this) {
                is ErrorEffect -> view.showError(errorMessage, event)
                is SuccessEffect -> view.applyEffect(bundle as E)
                is SuccessState -> {
                    val successStateBundle = bundle as S
                    view.setState(successStateBundle)
                    view.renderSuccessState(successStateBundle)
                }
            }
            view.toggleViews(this is LoadingEffect, event)
        }
    }
}


class PModelSubscriber<I, V : BaseView<I, S, E>, S : Parcelable, E>(private val view: V) : Consumer<PModel<*, I>> {
    override fun accept(pModel: PModel<*, I>) {
        pModel.apply {
            when (this) {
                is ErrorEffect -> view.showError(errorMessage, event)
                is SuccessEffect -> view.applyEffect(bundle as E)
                is SuccessState -> {
                    val successStateBundle = bundle as S
                    view.setState(successStateBundle)
                    view.renderSuccessState(successStateBundle)
                }
            }
            view.toggleViews(this is LoadingEffect, event)
        }
    }
}
