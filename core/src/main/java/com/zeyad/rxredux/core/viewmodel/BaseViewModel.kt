package com.zeyad.rxredux.core.viewmodel

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import com.zeyad.rxredux.core.BaseEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

abstract class BaseViewModel<I : BaseEvent<*>, R, S : Parcelable, E> : ViewModel(), IBaseViewModel<I, R, S, E> {

    override var disposables: CompositeDisposable = CompositeDisposable()

    override val currentStateStream: BehaviorSubject<Any> = BehaviorSubject.create()

    override fun onCleared() {
        super.onCleared()
        disposables.dispose()
    }
}
