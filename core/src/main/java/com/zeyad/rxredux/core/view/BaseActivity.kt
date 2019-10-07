package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

abstract class BaseActivity<I, R, S : Parcelable, E, VM : IBaseViewModel<I, R, S, E>> : AppCompatActivity(), BaseView<I, S, E> {

    override var eventObservable: Observable<I> = Observable.empty()
    override lateinit var disposable: Disposable
    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
        setupUI(savedInstanceState == null)
        viewState?.let { vmStart(viewModel, it, this, this) }
                ?: run { throw IllegalArgumentException("ViewState is not initialized") }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        onSaveInstanceStateImpl(bundle, viewState)
        super.onSaveInstanceState(bundle)
    }

    override fun onResume() {
        super.onResume()
        disposable = eventObservable.subscribe { viewModel.events.onNext(it) }
    }

    override fun onPause() {
        onPauseImpl()
        super.onPause()
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    /**
     * Setup the UI.
     *
     * @param isNew = savedInstanceState == null
     */
    abstract fun setupUI(isNew: Boolean)
}
