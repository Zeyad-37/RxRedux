package com.zeyad.rxredux.screens.navigation

import android.annotation.SuppressLint
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
import com.zeyad.rxredux.core.Message
import com.zeyad.rxredux.core.view.BaseFragment
import com.zeyad.rxredux.screens.detail.NavigateToEvent
import com.zeyad.rxredux.screens.list.GetPaginatedUsersEvent
import com.zeyad.rxredux.utils.showErrorSnackBar
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.view_progress.*
import java.util.concurrent.TimeUnit

/**
 * A placeholder fragment containing a simple view.
 */
class FirstActivityFragment : BaseFragment<BaseEvent<*>, Any, FirstState, FirstEffect, FirstVM>() {

    override fun initialize() {
        viewModel = FirstVM()
        viewState = EmptyFirstState
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventObservable = eventObservable.mergeWith(RxView.clicks(fab)
                .map<BaseEvent<*>> { NavigateToEvent(Intent(requireContext(), SecondActivity::class.java)) })
    }

    @SuppressLint("RxLeakedSubscription", "CheckResult")
    override fun onResume() {
        super.onResume()
        if (viewState == EmptyFirstState) {
            postOnResumeEvents.onNext(GetPaginatedUsersEvent(0))
            var isSecond = false
            Flowable.timer(2000, TimeUnit.MILLISECONDS, Schedulers.computation()).repeat(2)
                    .subscribe({
                        if (isSecond)
                            postOnResumeEvents.onNext(GetPaginatedUsersEvent(0))
                        else {
                            postOnResumeEvents.onNext(EmptyEvent)
                            isSecond = true
                        }
                    }, { it.printStackTrace() })
        }
    }

    override fun renderSuccessState(successState: FirstState) {
        Log.d("FirstFragment", "Other State = ${successState.javaClass}")
    }

    override fun toggleViews(isLoading: Boolean, event: BaseEvent<*>?) {
        Log.d("FirstFragment", "Loading $isLoading")
        linear_layout_loader.bringToFront()
        linear_layout_loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun applyEffect(effectBundle: FirstEffect) {
        startActivity((effectBundle as NavigateToEffect).intent)
    }

    override fun showError(errorMessage: Message, event: BaseEvent<*>) {
        showErrorSnackBar("Oops", fab, Snackbar.LENGTH_INDEFINITE)
    }
}
