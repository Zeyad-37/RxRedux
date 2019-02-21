package com.zeyad.rxredux.core.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

abstract class BaseAndroidViewModel<S>(app: Application) : AndroidViewModel(app), IBaseViewModel<S> {

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}