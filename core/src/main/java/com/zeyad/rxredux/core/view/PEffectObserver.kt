package com.zeyad.rxredux.core.view

import android.arch.lifecycle.Observer
import com.zeyad.rxredux.core.PEffect

class PEffectObserver<V : BaseView<S>, S>(private val view: V) : Observer<PEffect<S>> {
    override fun onChanged(uiModel: PEffect<S>?) {
        uiModel?.apply {
            view.setState(bundle)
            view.renderSuccessState(bundle)
        }
    }
}
