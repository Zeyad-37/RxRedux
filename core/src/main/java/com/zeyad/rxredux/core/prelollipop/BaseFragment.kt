package com.zeyad.rxredux.core.prelollipop

import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.app.Fragment
import com.zeyad.rxredux.core.*
import com.zeyad.rxredux.core.BaseView.Companion.UI_MODEL
import io.reactivex.Observable

/**
 * @author ZIaDo on 2/27/18.
 */
abstract class BaseFragment<S : Parcelable, VM : BaseViewModel<S>> : Fragment(), LoadDataView<S> {

    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        viewState = BaseView.getViewStateFrom(savedInstanceState, arguments)
        initialize()
    }

    override fun onStart() {
        super.onStart()
        LiveDataReactiveStreams.fromPublisher(viewModel.uiModels(viewState))
                .observe(this, UIObserver(this, errorMessageFactory()))
        viewModel.processEvents(events())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (viewState != null) {
            outState.putParcelable(UI_MODEL, viewState)
        }
        super.onSaveInstanceState(outState)
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
}