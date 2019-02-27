package com.zeyad.rxredux.core.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

abstract class BaseAndroidViewModel<S, E>(app: Application) : AndroidViewModel(app), IBaseViewModel<S, E> {

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}