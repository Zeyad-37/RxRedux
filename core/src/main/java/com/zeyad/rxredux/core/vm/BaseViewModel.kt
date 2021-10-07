package com.zeyad.rxredux.core.vm

import android.os.Parcelable
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.zeyad.rxredux.core.view.P_MODEL
import com.zeyad.rxredux.core.vm.rxvm.Effect
import com.zeyad.rxredux.core.vm.rxvm.Error
import com.zeyad.rxredux.core.vm.rxvm.Input
import com.zeyad.rxredux.core.vm.rxvm.Progress
import com.zeyad.rxredux.core.vm.rxvm.State
import com.zeyad.rxredux.core.vm.rxvm.ViewModelListenerHelper
import com.zeyad.rxredux.core.vm.rxvm.ViewModelListener
import com.zeyad.rxredux.core.vm.viewmodel.ErrorEffect
import com.zeyad.rxredux.core.vm.viewmodel.IBaseViewModel
import com.zeyad.rxredux.core.vm.viewmodel.LoadingEffect
import com.zeyad.rxredux.core.vm.viewmodel.Result
import com.zeyad.rxredux.core.vm.viewmodel.SuccessEffect
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.addTo
import io.reactivex.subjects.PublishSubject
import kotlin.properties.Delegates.observable

@Suppress("UNCHECKED_CAST")
abstract class BaseViewModel<I, R, S : Parcelable, E> constructor(private val savedStateHandle: SavedStateHandle? = null)
    : ViewModel(), IBaseViewModel<I, R, S, E> {
    var viewModelListener: ViewModelListener? = null

    private object EmptyInput : Input()

    private val compositeDisposable = CompositeDisposable()

    override val inputs: PublishSubject<I> = PublishSubject.create()

    override lateinit var currentPModel: S
    private var isLoading: Boolean by observable(false, onChange = { _, oldValue, newValue ->
        if (newValue != oldValue) {
            notifyProgressChanged(newValue)
        }
    })

    override var disposable: Disposable = Disposables.empty()

    fun bind(initialState: S, inputs: () -> Observable<I> = { Observable.empty() }): BaseViewModel<I, R, S, E> {
        currentPModel = if (savedStateHandle != null && savedStateHandle.contains(P_MODEL)) {
            savedStateHandle.get<S>(P_MODEL) as S
        } else {
            initialState
        }
        inputs().mergeWith(this.inputs).toResult().run {
            observeStates(this as Flowable<Result<R, I>>)
            observeEffects(this as Flowable<Result<E, I>>)
            observeErrors(this)
            observeProgress(this)
        }
        return this
    }

    fun observe(lifecycleOwner: LifecycleOwner, init: ViewModelListenerHelper.() -> Unit) {
        val helper = ViewModelListenerHelper()
        helper.init()
        this.viewModelListener = helper
        removeInputObserversOnDestroy(lifecycleOwner)
    }

    private fun observeProgress(results: Flowable<Result<E, I>>) {
        effectStream(results)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isLoading = it is LoadingEffect }
                .addTo(compositeDisposable)
    }

    private fun observeErrors(results: Flowable<Result<E, I>>) {
        effectStream(results)
                .observeOn(AndroidSchedulers.mainThread())
                .ofType(ErrorEffect::class.java)
                .map { Error(it.errorMessage, it.error) }
                .subscribe(::notifyError)
                .addTo(compositeDisposable)
    }

    private fun observeEffects(results: Flowable<Result<E, I>>) {
        effectStream(results)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext { middleware(it) }
                .ofType(SuccessEffect::class.java)
                .map { it.bundle }
                .ofType(Effect::class.java)
                .subscribe(::notifyEffect)
                .addTo(compositeDisposable)
    }

    private fun observeStates(results: Flowable<Result<R, I>>) {
        stateStream(results, currentPModel)
                .observeOn(AndroidSchedulers.mainThread())
                .doAfterNext { middleware(it) }
                .subscribe {
                    if (it.bundle is State) {
                        saveState(it.bundle as State)
                        notifyNewState(it.bundle as State)
                    }
                    isLoading = false
                }.addTo(compositeDisposable)
    }

    private fun notifyProgressChanged(isLoading: Boolean) {
        viewModelListener?.progress?.invoke(Progress(isLoading, EmptyInput))
    }

    private fun notifyEffect(effect: Effect) {
        viewModelListener?.effects?.invoke(effect)
    }

    private fun notifyError(error: Error) {
        viewModelListener?.errors?.invoke(error)
    }

    private fun notifyNewState(state: State) {
        viewModelListener?.states?.invoke(state)
    }

    private fun saveState(state: State) {
        savedStateHandle?.set(P_MODEL, state)
    }

    override fun onCleared() {
        super.onCleared()
        onClearImpl()
        compositeDisposable.dispose()
    }

    private fun removeInputObserversOnDestroy(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleObserver {
            @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
            fun onDestroy() {
                viewModelListener = null
                compositeDisposable.clear()
                lifecycleOwner.lifecycle.removeObserver(this)
            }
        })
    }
}
