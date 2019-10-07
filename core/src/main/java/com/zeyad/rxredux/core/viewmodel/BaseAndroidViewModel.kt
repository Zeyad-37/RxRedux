package com.zeyad.rxredux.core.viewmodel

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.SerialDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

abstract class BaseAndroidViewModel<I, R, S : Parcelable, E>(app: Application) : AndroidViewModel(app), IBaseViewModel<I, R, S, E> {

    override val currentStateStream: BehaviorSubject<Any> = BehaviorSubject.create()

    override val events: PublishSubject<I> = PublishSubject.create()

    override var disposable: Disposable = SerialDisposable()

    override fun onCleared() {
        onClearImpl()
        super.onCleared()
    }
}
