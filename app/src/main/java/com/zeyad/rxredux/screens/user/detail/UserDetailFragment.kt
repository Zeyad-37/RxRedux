package com.zeyad.rxredux.screens.user.detail

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.Toolbar
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.GlideDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.zeyad.gadapter.GenericRecyclerViewAdapter
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.BaseView.Companion.UI_MODEL
import com.zeyad.rxredux.core.ErrorMessageFactory
import com.zeyad.rxredux.screens.BaseFragment
import com.zeyad.rxredux.screens.ViewModelFactory
import com.zeyad.rxredux.screens.user.list.UserListActivity
import com.zeyad.rxredux.utils.Utils
import com.zeyad.usecases.api.DataServiceFactory
import io.reactivex.Observable
import kotlinx.android.synthetic.main.user_detail.*
import kotlinx.android.synthetic.main.view_progress.*
import java.util.*

/**
 * A fragment representing a single Repository detail screen. This fragment is either contained in a
 * [UserListActivity] in two-pane mode (on tablets) or a [UserDetailActivity] on
 * handsets.
 */
/**
 * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
 * screen orientation changes).
 */
class UserDetailFragment : BaseFragment<UserDetailState, UserDetailVM>() {

    private var repositoriesAdapter: GenericRecyclerViewAdapter? = null

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
        postponeEnterTransition()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        }
        //        setSharedElementReturnTransition(null); // supply the correct element for return transition
    }

    override fun errorMessageFactory(): ErrorMessageFactory {
        return object : ErrorMessageFactory {
            override fun getErrorMessage(throwable: Throwable): String {
                return throwable.localizedMessage
            }
        }
    }

    override fun initialize() {
        viewModel = ViewModelProviders.of(this,
                ViewModelFactory(DataServiceFactory.getInstance()!!)).get(UserDetailVM::class.java)
    }

    override fun events(): Observable<BaseEvent<*>> {
        return Observable.just(GetReposEvent(viewState!!.user!!.login!!))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.user_detail, container, false)
        ButterKnife.bind(this, rootView)
        setupRecyclerView()
        return rootView
    }

    private fun setupRecyclerView() {
        recyclerView_repositories.layoutManager = LinearLayoutManager(context)
        repositoriesAdapter = object : GenericRecyclerViewAdapter(
                context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater,
                ArrayList<ItemInfo>()) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<*> {
                return RepositoryViewHolder(layoutInflater.inflate(viewType, parent, false))
            }
        }
        recyclerView_repositories.adapter = repositoriesAdapter
    }

    override fun renderSuccessState(successState: UserDetailState) {
        repositoriesAdapter!!.animateTo(successState.repos)
        val user = successState.user
        if (successState.isTwoPane) {
            Utils.runIfNotNull(activity as UserListActivity) { activity ->
                val appBarLayout = activity.findViewById<Toolbar>(R.id.toolbar)
                if (appBarLayout != null) {
                    appBarLayout.title = user!!.login
                }
                if (user!!.avatarUrl!!.isNotBlank()) {
                    Glide.with(context).load(user.avatarUrl).dontAnimate().listener(requestListener)
                            .into(activity.getImageViewAvatar())
                }
            }
        } else {
            Utils.runIfNotNull(activity as UserDetailActivity) { activity ->
                val appBarLayout = activity.getCollapsingToolbarLayout()
                appBarLayout.title = user!!.login
                if (user.avatarUrl!!.isNotBlank()) {
                    Glide.with(context).load(user.avatarUrl).dontAnimate().listener(requestListener)
                            .into(activity.getImageViewAvatar())
                }
            }
        }
        //        applyPalette();
    }

    internal fun glideRequestListenerCore(): Boolean {
        Utils.runIfNotNull(activity!!, { it.supportStartPostponedEnterTransition() })
        return false
    }

    override fun toggleViews(isLoading: Boolean) {
        linear_layout_loader.bringToFront()
        linear_layout_loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun showError(errorMessage: String) {
        showErrorSnackBar(errorMessage, linear_layout_loader, Snackbar.LENGTH_LONG)
    }

    private fun applyPalette() {
        if (Utils.hasM()) {
            val activity = activity as UserDetailActivity?
            val drawable = activity!!.getImageViewAvatar().drawable as BitmapDrawable
            val bitmap = drawable.bitmap
            Palette.from(bitmap).generate { palette ->
                activity.findViewById<View>(R.id.coordinator_detail)
                        .setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                            if (v.height == scrollX) {
                                activity.getToolbar().setTitleTextColor(palette.getLightVibrantColor(Color.TRANSPARENT))
                                activity.getToolbar().background = ColorDrawable(palette.getLightVibrantColor(Color.TRANSPARENT))
                            } else if (scrollY == 0) {
                                activity.getToolbar().setTitleTextColor(0)
                                activity.getToolbar().background = null
                            }
                        }
            }
        }
    }

    companion object {

        fun newInstance(userDetailState: UserDetailState): UserDetailFragment {
            val userDetailFragment = UserDetailFragment()
            val bundle = Bundle()
            bundle.putParcelable(UI_MODEL, userDetailState)
            userDetailFragment.arguments = bundle
            return userDetailFragment
        }
    }
}
