package com.zeyad.rxredux.core

import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatDelegate
import com.zeyad.rxredux.core.BaseView.Companion.UI_MODEL
import io.reactivex.Observable

/**
 * @author ZIaDo on 2/27/18.
 */
abstract class BaseFragmentActivity<S : Parcelable, VM : BaseViewModel<S>> : FragmentActivity(),
        LoadDataView<S> {

    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        viewState = BaseView.getViewStateFrom(savedInstanceState, intent)
        initialize()
        setupUI(savedInstanceState == null)
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        if (bundle != null && viewState != null) {
            bundle.putParcelable(UI_MODEL, viewState)
        }
        super.onSaveInstanceState(bundle)
    }

    override fun onStart() {
        super.onStart()
        LiveDataReactiveStreams.fromPublisher(viewModel.uiModels(viewState))
                .observe(this, UIObserver(this, errorMessageFactory()))
        viewModel.processEvents(events())
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewState = BaseView.getViewStateFrom(savedInstanceState, intent)
    }

    abstract fun errorMessageFactory(): ErrorMessageFactory

    /**
     * Initialize objects or any required dependencies.
     */
    abstract fun initialize()

    /**
     * Setup the UI.
     * @param isNew = savedInstanceState == null
     */
    abstract fun setupUI(isNew: Boolean)

    /**
     * Merge all events into one [Observable].
     *
     * @return [Observable].
     */
    abstract fun events(): Observable<BaseEvent<*>>
}