package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.ViewModel

abstract class BaseViewModel<S> : ViewModel(), IBaseViewModel<S> {

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
