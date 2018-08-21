package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import io.reactivex.Observable

/**
 * @author Zeyad Gasser.
 */
abstract class BaseActivity<S, VM : BaseViewModel<S>> : AppCompatActivity(), LoadDataView<S> {
    lateinit var viewModel: VM
    var viewState: S? = null
    lateinit var stateEvent: BaseEvent<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        savedInstanceState?.getLastStateEvent()?.let { stateEvent = it }
        initialize()
        setupUI(savedInstanceState == null)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedInstanceState.getLastStateEvent()?.let { stateEvent = it }
    }

    override fun onSaveInstanceState(bundle: Bundle) {
        if (::stateEvent.isInitialized)
            bundle.putParcelable(UI_EVENT, stateEvent)
        super.onSaveInstanceState(bundle)
    }

    override fun onStart() {
        super.onStart()
        viewModel.processEvents(when (::stateEvent.isInitialized) {
            true -> events().startWith(stateEvent)
            false -> events()
        }, viewState).toLiveData()
                .observe(this, UIObserver<LoadDataView<S>, S>(this, errorMessageFactory()))
    }

    override fun setStateWithEvent(bundle: S, event: BaseEvent<*>) {
        viewState = bundle
        stateEvent = event
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
}