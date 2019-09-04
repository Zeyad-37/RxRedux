package com.zeyad.rxredux.core.viewmodel

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.BehaviorSubject

abstract class BaseViewModel<I, R, S : Parcelable, E> : ViewModel(), IBaseViewModel<I, R, S, E> {

    override var disposables: SerialDisposable = SerialDisposable()

    override val currentStateStream: BehaviorSubject<Any> = BehaviorSubject.create()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}
