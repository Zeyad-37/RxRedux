package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel<S> : ViewModel(), IBaseViewModel<S> {

    init {
        disposable = CompositeDisposable()
    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
