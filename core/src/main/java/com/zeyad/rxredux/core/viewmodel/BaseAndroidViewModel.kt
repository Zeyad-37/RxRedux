package com.zeyad.rxredux.core.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Parcelable
import io.reactivex.subjects.BehaviorSubject

abstract class BaseAndroidViewModel<R, S : Parcelable, E>(app: Application) :
        AndroidViewModel(app), IBaseViewModel<R, S, E> {

    override val currentStateStream: BehaviorSubject<S> = BehaviorSubject.create()

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
