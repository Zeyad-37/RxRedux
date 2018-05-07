package com.zeyad.rxredux.core.prelollipop

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.zeyad.rxredux.core.*
import com.zeyad.rxredux.core.BaseView.Companion.UI_MODEL
import io.reactivex.Observable

/**
 * @author ZIaDo on 2/27/18.
 */
abstract class BaseActivity<S : Parcelable, VM : BaseViewModel<S>> :
        AppCompatActivity(), LoadDataView<S>, LifecycleOwner {

    private lateinit var mLifecycleRegistry: LifecycleRegistry
    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLifecycleRegistry = LifecycleRegistry(this)
        mLifecycleRegistry.markState(Lifecycle.State.CREATED)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        viewState = BaseView.getViewStateFrom(savedInstanceState, intent)
        initialize()
        setupUI(savedInstanceState == null)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        viewState = BaseView.getViewStateFrom(savedInstanceState, Bundle.EMPTY)
    }

    override fun onSaveInstanceState(bundle: Bundle?) {
        if (bundle != null && viewState != null) {
            bundle.putParcelable(UI_MODEL, viewState)
        }
        super.onSaveInstanceState(bundle)
    }

    override fun onStart() {
        super.onStart()
        mLifecycleRegistry.markState(Lifecycle.State.STARTED)
        LiveDataReactiveStreams.fromPublisher(viewModel.uiModels(viewState)).observe(this,
                UIObserver<LoadDataView<S>, S>(this, errorMessageFactory()))
        viewModel.processEvents(events())
    }

    override fun onResume() {
        super.onResume()
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    override fun onDestroy() {
        super.onDestroy()
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED)
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }

    abstract fun errorMessageFactory(): ErrorMessageFactory

    /**
     * Initialize objects or any required dependencies.
     */
    abstract fun initialize()

    /**
     * Setup the UI.
     *
     * @param isNew = savedInstanceState == null
     */
    abstract fun setupUI(isNew: Boolean)

    /**
     * Merge all events into one [Observable].
     *
     * @return [Observable].
     */
    abstract fun events(): Observable<BaseEvent<*>>

    override fun getLifecycle(): Lifecycle = mLifecycleRegistry
}