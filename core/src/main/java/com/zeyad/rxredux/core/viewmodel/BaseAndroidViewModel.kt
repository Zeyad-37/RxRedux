package com.zeyad.rxredux.core.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent

abstract class BaseAndroidViewModel<R, S : Parcelable, E>(app: Application) :
        AndroidViewModel(app), IBaseViewModel<R, S, E> {

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
