package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent

abstract class BaseViewModel<R, S : Parcelable, E> : ViewModel(), IBaseViewModel<R, S, E> {

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
