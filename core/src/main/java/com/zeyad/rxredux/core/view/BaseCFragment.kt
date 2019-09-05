package com.zeyad.rxredux.core.view

import android.os.Bundle
import android.os.Parcelable
import com.zeyad.rxredux.core.ErrorEffect
import com.zeyad.rxredux.core.LoadingEffect
import com.zeyad.rxredux.core.SuccessEffect
import com.zeyad.rxredux.core.SuccessState
import com.zeyad.rxredux.core.viewmodel.coroutines.Machine
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
abstract class BaseCFragment<I, R, S : Parcelable, E, VM : Machine<I, R, S, E>> : androidx.fragment.app.Fragment(), BaseView<I, S, E>, CoroutineScope {

    override val coroutineContext: CoroutineContext = Job() + Dispatchers.Main
    override val postOnResumeEvents = PublishSubject.create<I>()
    override var eventObservable: Observable<I> = Observable.empty()
    lateinit var viewModel: VM
    var viewState: S? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
        initialize()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        onSaveInstanceStateImpl(outState, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        getViewStateFrom<S>(savedInstanceState)?.let { viewState = it }
    }

    override fun onStart() {
        super.onStart()
        viewModel.store(viewState!!)
        launch(Dispatchers.Main) {
            viewModel.pModels.consumeEach { uiModel ->
                uiModel.apply {
                    when (this) {
                        is ErrorEffect -> showError(errorMessage, event)
                        is SuccessEffect -> applyEffect(bundle as E)
                        is SuccessState -> {
                            val successStateBundle = bundle as S
                            setState(successStateBundle)
                            renderSuccessState(successStateBundle)
                        }
                    }
                    toggleViews(this is LoadingEffect, event)
                }
            }
        }
    }

    override fun setState(bundle: S) {
        viewState = bundle
    }
}
