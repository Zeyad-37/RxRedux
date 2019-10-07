package com.zeyad.rxredux.core.viewmodel

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class BaseViewModel<I, R, S : Parcelable, E> : ViewModel(), IBaseViewModel<I, R, S, E> {

    override val currentStateStream: BehaviorSubject<Any> = BehaviorSubject.create()

    override val events: PublishSubject<I> = PublishSubject.create()

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }
}
