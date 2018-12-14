package com.zeyad.rxredux.core.view

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import io.reactivex.Observable
import org.reactivestreams.Publisher

const val UI_MODEL = "viewState"

fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?): S? =
        if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL))
            savedInstanceState.getParcelable(UI_MODEL)
        else null

fun <T> Publisher<T>.toLiveData() = LiveDataReactiveStreams.fromPublisher(this) as LiveData<T>

typealias ErrorMessageFactory = (throwable: Throwable, event: BaseEvent<*>) -> String

interface BaseView<S : Parcelable, VM : BaseViewModel<S>> : LoadDataView<S>, LifecycleOwner {
    var viewModel: VM?
    var viewState: S?

    fun onSaveInstanceStateImpl(bundle: Bundle) = bundle.putParcelable(UI_MODEL, viewState)

    fun onStartImpl() {
        viewState = initialState()
        viewModel?.processEvents(events(), initialState())?.toLiveData()
                ?.observe(this, UIObserver<LoadDataView<S>, S>(this, errorMessageFactory()))
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    fun errorMessageFactory(): ErrorMessageFactory

    /**
     * Initialize objects or any required dependencies.
     */
    fun initialize()

    /**
     * Merge all events into one [Observable].
     *
     * @return [Observable].
     */
    fun events(): Observable<BaseEvent<*>>

    /**
     * @return initial state of view
     */
    fun initialState(): S
}
