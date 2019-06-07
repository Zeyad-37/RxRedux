package com.zeyad.rxredux.screens.detail

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zeyad.gadapter.GenericRecyclerViewAdapter
import com.zeyad.gadapter.GenericViewHolder
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.Message
import com.zeyad.rxredux.core.view.IBaseFragment
import com.zeyad.rxredux.core.view.P_MODEL
import com.zeyad.rxredux.screens.list.UserListActivity
import com.zeyad.rxredux.screens.list.UserListActivity2
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.user_detail.*
import kotlinx.android.synthetic.main.view_progress.*
import org.koin.android.viewmodel.ext.android.getViewModel

/**
 * A fragment representing a single Repository detail screen. This fragment is either contained in a
 * [UserListActivity] in two-pane mode (on tablets) or a [UserDetailActivity2] on
 * handsets.
 */
@SuppressLint("ValidFragment")
class UserDetailFragment2 : Fragment(), IBaseFragment<UserDetailEvents, UserDetailResult, UserDetailState, UserDetailEffect, UserDetailVM> {

    override var viewModel: UserDetailVM? = null
    override var viewState: UserDetailState? = null
    override val postOnResumeEvents: PublishSubject<UserDetailEvents> = PublishSubject.create()
    override var eventObservable: Observable<UserDetailEvents> = Observable.empty<UserDetailEvents>()

    private lateinit var repositoriesAdapter: GenericRecyclerViewAdapter

    private val requestListener = object : RequestListener<String, GlideDrawable> {
        override fun onException(e: Exception, model: String, target: Target<GlideDrawable>,
                                 isFirstResource: Boolean): Boolean {
            return glideRequestListenerCore()
        }

        override fun onResourceReady(resource: GlideDrawable, model: String, target: Target<GlideDrawable>,
                                     isFromMemoryCache: Boolean, isFirstResource: Boolean): Boolean {
            return glideRequestListenerCore()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onCreateImpl(savedInstanceState)
        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
        //        setSharedElementReturnTransition(null); // supply the correct element for return transition
    }

    override fun onStart() {
        super.onStart()
        onStartImpl()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        onSaveInstanceStateImpl(outState, viewState)
        super.onSaveInstanceState(outState)
    }

    override fun initialize() {
        viewModel = getViewModel()
        viewState = arguments?.getParcelable(P_MODEL)!!
        eventObservable = Observable.just(GetReposEvent((viewState as IntentBundleState).user.login))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.user_detail, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView_repositories.layoutManager = LinearLayoutManager(context)
        repositoriesAdapter = object : GenericRecyclerViewAdapter(
                requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                ArrayList<ItemInfo>()) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<*> {
                return RepositoryViewHolder(layoutInflater.inflate(viewType, parent, false))
            }
        }
        recyclerView_repositories.adapter = repositoriesAdapter
    }

    override fun renderSuccessState(successState: UserDetailState) {
        when (successState) {
            is FullDetailState -> {
                repositoriesAdapter.setDataList(successState.repos, null)
                val user = successState.user
                if (successState.isTwoPane) {
                    (activity as UserListActivity2).let { activity ->
                        val appBarLayout = activity.findViewById<Toolbar>(R.id.toolbar)
                        if (appBarLayout != null) {
                            appBarLayout.title = user.login
                        }
                        if (user.avatarUrl.isNotBlank()) {
                            Glide.with(context).load(user.avatarUrl).dontAnimate().listener(requestListener)
                                    .into(activity.getImageViewAvatar())
                        }
                    }
                } else {
                    (activity as UserDetailActivity).let { activity ->
                        val appBarLayout = activity.getCollapsingToolbarLayout()
                        appBarLayout.title = user.login
                        if (user.avatarUrl.isNotBlank()) {
                            Glide.with(context).load(user.avatarUrl).dontAnimate().listener(requestListener)
                                    .into(activity.getImageViewAvatar())
                        }
                    }
                }
            }
        }
    }

    override fun applyEffect(effectBundle: UserDetailEffect) {
        if (effectBundle is NavigateFromDetail)
            startActivity(effectBundle.intent)
    }

    internal fun glideRequestListenerCore(): Boolean {
        activity?.supportStartPostponedEnterTransition()
        return false
    }

    override fun toggleViews(isLoading: Boolean, event: UserDetailEvents) {
        linear_layout_loader.bringToFront()
        linear_layout_loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showError(errorMessage: Message, event: UserDetailEvents) {
//        showErrorSnackBar(errorMessage, linear_layout_loader, Snackbar.LENGTH_LONG)
    }

    companion object {

        fun newInstance(userDetailState: UserDetailState): UserDetailFragment2 =
                UserDetailFragment2().apply { arguments = Bundle().apply { putParcelable(P_MODEL, userDetailState) } }
    }
}
