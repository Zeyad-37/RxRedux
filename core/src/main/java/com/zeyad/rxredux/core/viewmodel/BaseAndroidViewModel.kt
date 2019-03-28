package com.zeyad.rxredux.core.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

abstract class BaseAndroidViewModel<R, S, E>(app: Application) : AndroidViewModel(app), IBaseViewModel<R, S, E> {

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
