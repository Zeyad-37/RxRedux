package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

abstract class BaseViewModel<R, S : Parcelable, E> : ViewModel(), IBaseViewModel<R, S, E> {

    override var disposable: CompositeDisposable = CompositeDisposable()

    override val currentStateStream: BehaviorSubject<Any> = BehaviorSubject.create()

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
