package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.ViewModel

abstract class BaseViewModel<S, E> : ViewModel(), IBaseViewModel<S, E> {

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
