package com.zeyad.rxredux.core

import android.app.Fragment
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.BaseView.Companion.UI_MODEL
import io.reactivex.Observable

/**
 * @author ZIaDo on 2/27/18.
 */
abstract class BaseFragment<S : Parcelable, VM : BaseViewModel<S>> : Fragment(), LoadDataView<S>, LifecycleOwner {

    private lateinit var mLifecycleRegistry: LifecycleRegistry
    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mLifecycleRegistry = LifecycleRegistry(this)
        mLifecycleRegistry.markState(Lifecycle.State.CREATED)
        retainInstance = true
        viewState = BaseView.getViewStateFrom(savedInstanceState, arguments)
        initialize()
    }

    override fun onStart() {
        super.onStart()
        mLifecycleRegistry.markState(Lifecycle.State.STARTED)
        LiveDataReactiveStreams.fromPublisher(viewModel.uiModels(viewState))
                .observe(this, UIObserver(this, errorMessageFactory()))
        viewModel.processEvents(events())
    }

    override fun onResume() {
        super.onResume()
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        if (outState != null && viewState != null) {
            outState.putParcelable(UI_MODEL, viewState)
        }
        super.onSaveInstanceState(outState)
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
     * Initialize any objects or any required dependencies.
     */
    abstract fun initialize()

    /**
     * Merge all events into one [Observable].
     *
     * @return [Observable].
     */
    abstract fun events(): Observable<BaseEvent<*>>

    override fun getLifecycle(): Lifecycle = mLifecycleRegistry
}
