package com.zeyad.rxredux.screens.navigation

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding2.view.clicks
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.view.BaseFragment
import com.zeyad.rxredux.screens.detail.NavigateToIntent
import com.zeyad.rxredux.screens.list.GetPaginatedUsersIntent
import com.zeyad.rxredux.utils.showErrorSnackBar
import io.reactivex.Flowable
import kotlinx.android.synthetic.main.fragment_first.*
import kotlinx.android.synthetic.main.view_progress.*
import java.util.concurrent.TimeUnit

/**
 * A placeholder fragment containing a simple view.
 */
class FirstActivityFragment : BaseFragment<Any, Any, FirstState, FirstEffect, FirstVM>() {

    override fun initialize() {
        viewModel = FirstVM()
        viewState = EmptyFirstState
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_first, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        intentStream = fab.clicks().map { NavigateToIntent(Intent(requireContext(), SecondActivity::class.java)) }
    }

    @SuppressLint("RxLeakedSubscription", "CheckResult")
    override fun onResume() {
        super.onResume()
        if (viewState == EmptyFirstState) {
            viewModel.offer(GetPaginatedUsersIntent(0))
            var isSecond = false
            Flowable.timer(2000, TimeUnit.MILLISECONDS).repeat(2)
                    .subscribe({
                        if (isSecond)
                            viewModel.offer(GetPaginatedUsersIntent(0))
                        else {
                            viewModel.offer(Any())
                            isSecond = true
                        }
                    }, { it.printStackTrace() })
        }
    }

    override fun bindState(successState: FirstState) {
        Log.d("FirstFragment", "Other State = ${successState.javaClass}")
    }

    override fun toggleLoadingViews(isLoading: Boolean, intent: Any?) {
        Log.d("FirstFragment", "Loading $isLoading")
        linear_layout_loader.bringToFront()
        linear_layout_loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun bindEffect(effectBundle: FirstEffect) {
        startActivity((effectBundle as NavigateToEffect).intent)
    }

    override fun bindError(errorMessage: String, intent: Any, cause: Throwable) {
        showErrorSnackBar("Oops", fab, Snackbar.LENGTH_INDEFINITE)
    }
}
