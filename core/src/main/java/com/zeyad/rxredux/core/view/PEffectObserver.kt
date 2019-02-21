package com.zeyad.rxredux.core.view

import android.arch.lifecycle.Observer
import com.zeyad.rxredux.core.ErrorEffect
import com.zeyad.rxredux.core.LoadingEffect
import com.zeyad.rxredux.core.PEffect
import com.zeyad.rxredux.core.SuccessEffect

class PEffectObserver<V : BaseView<*>>(private val view: V) : Observer<PEffect<*>> {
    override fun onChanged(uiModel: PEffect<*>?) {
        uiModel?.apply {
            view.toggleViews(this is LoadingEffect, event)
            when {
                this is ErrorEffect -> view.showError(errorMessage, event)
                this is SuccessEffect -> view.applyEffect(bundle!!)
            }
        }
    }
}
