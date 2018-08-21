package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.support.v4.app.Fragment
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import io.reactivex.Observable

/**
 * @author Zeyad Gasser.
 */
abstract class BaseFragment<S, VM : BaseViewModel<S>> : Fragment(), LoadDataView<S> {

    lateinit var viewModel: VM
    var viewState: S? = null
    lateinit var stateEvent: BaseEvent<*>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        savedInstanceState?.getLastStateEvent()?.let { stateEvent = it }
        initialize()
    }

    override fun onStart() {
        super.onStart()
        viewModel.processEvents(when (::stateEvent.isInitialized) {
            true -> events().startWith(stateEvent)
            false -> events()
        }, viewState).toLiveData()
                .observe(this, UIObserver<LoadDataView<S>, S>(this, errorMessageFactory()))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (::stateEvent.isInitialized)
            outState.putParcelable(UI_EVENT, stateEvent)
        super.onSaveInstanceState(outState)
    }

    override fun setStateWithEvent(bundle: S, event: BaseEvent<*>) {
        viewState = bundle
        stateEvent = event
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
}