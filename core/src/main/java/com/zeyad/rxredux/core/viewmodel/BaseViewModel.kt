package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.ViewModel

abstract class BaseViewModel<R, S, E> : ViewModel(), IBaseViewModel<R, S, E> {

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
