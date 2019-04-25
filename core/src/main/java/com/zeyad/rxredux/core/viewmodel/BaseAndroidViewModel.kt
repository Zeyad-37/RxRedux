package com.zeyad.rxredux.core.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.os.Parcelable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

abstract class BaseAndroidViewModel<R, S : Parcelable, E>(app: Application) :
        AndroidViewModel(app), IBaseViewModel<R, S, E> {

    override var disposables: CompositeDisposable = CompositeDisposable()

    override val currentStateStream: BehaviorSubject<Any> = BehaviorSubject.create()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}
