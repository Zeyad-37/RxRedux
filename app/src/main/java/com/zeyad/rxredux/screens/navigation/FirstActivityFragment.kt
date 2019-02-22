package com.zeyad.rxredux.screens.navigation

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.RxView
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.EmptyEvent
import com.zeyad.rxredux.screens.BaseFragment
import com.zeyad.rxredux.screens.user.detail.NavigateToEvent
import com.zeyad.rxredux.screens.user.list.GetPaginatedUsersEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.fragment_first.*

/**
 * A placeholder fragment containing a simple view.
 */
class FirstActivityFragment : BaseFragment<FirstState, FirstVM>() {

    private var eventObservable: Observable<BaseEvent<*>> = Observable.empty()
    private val postOnResumeEvents: PublishSubject<BaseEvent<*>> = PublishSubject.create()
    override fun initialize() {
        viewModel = FirstVM()
        viewState = EmptyFirstState
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun setupUI(isNew: Boolean) {
        eventObservable = eventObservable.mergeWith(RxView.clicks(fab)
                .map<BaseEvent<*>> { NavigateToEvent(Intent(requireContext(), SecondActivity::class.java)) })
    }

    override fun onResume() {
        super.onResume()
        if (viewState == EmptyFirstState) {
            postOnResumeEvents.onNext(GetPaginatedUsersEvent(0))
            postOnResumeEvents.onNext(EmptyEvent)
        }
    }

    override fun events(): Observable<BaseEvent<*>> = eventObservable.mergeWith(postOnResumeEvents)

    override fun renderSuccessState(successState: FirstState) {
        Log.d("FirstFragment", "Other State = ${successState.javaClass}")
    }

    override fun applyEffect(effectBundle: Any) {
        startActivity(effectBundle as Intent)
    }

    override fun toggleViews(isLoading: Boolean, event: BaseEvent<*>) {
        Log.d("FirstFragment", "Loading $isLoading")
    }

    override fun showError(errorMessage: String, event: BaseEvent<*>) {
        showErrorSnackBar("Oops", fab, Snackbar.LENGTH_INDEFINITE)
    }
}
