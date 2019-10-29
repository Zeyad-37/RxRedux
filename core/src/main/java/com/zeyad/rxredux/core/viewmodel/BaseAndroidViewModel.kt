package com.zeyad.rxredux.core.viewmodel

import android.app.Application
import android.os.Parcelable
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

abstract class BaseAndroidViewModel<I, R, S : Parcelable, E>(app: Application) : AndroidViewModel(app), IBaseViewModel<I, R, S, E> {

    override var currentPModel: Any = Any()

    override val intents: PublishSubject<I> = PublishSubject.create()

    override var disposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        onClearImpl()
        super.onCleared()
    }
}
