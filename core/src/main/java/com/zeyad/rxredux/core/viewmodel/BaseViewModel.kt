package com.zeyad.rxredux.core.viewmodel

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

abstract class BaseViewModel<I, R, S : Parcelable, E> : ViewModel(), IBaseViewModel<I, R, S, E> {

    override var currentPModel: Any = Any()

    override val intents: PublishSubject<I> = PublishSubject.create()

    override lateinit var disposable: Disposable

    override fun onCleared() {
        onClearImpl()
        super.onCleared()
    }
}
