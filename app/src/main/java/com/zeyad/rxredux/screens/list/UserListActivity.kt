package com.zeyad.rxredux.screens.list

import android.app.SearchManager
import android.content.Context
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.ItemTouchHelper
import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView
import com.zeyad.gadapter.*
import com.zeyad.gadapter.GenericAdapter.Companion.SECTION_HEADER
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.view.BaseActivity
import com.zeyad.rxredux.screens.User
import com.zeyad.rxredux.screens.detail.IntentBundleState
import com.zeyad.rxredux.screens.detail.UserDetailActivity
import com.zeyad.rxredux.screens.detail.UserDetailFragment
import com.zeyad.rxredux.screens.list.viewHolders.EmptyViewHolder
import com.zeyad.rxredux.screens.list.viewHolders.SectionHeaderViewHolder
import com.zeyad.rxredux.screens.list.viewHolders.UserViewHolder
import com.zeyad.rxredux.utils.addFragment
import com.zeyad.rxredux.utils.removeFragment
import com.zeyad.rxredux.utils.showErrorSnackBarWithAction
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_user_list.*
import kotlinx.android.synthetic.main.user_list.*
import kotlinx.android.synthetic.main.view_progress.*
import org.koin.android.viewmodel.ext.android.getViewModel
import java.util.concurrent.TimeUnit

/**
 * An activity representing a list of Repos. This activity has different presentations for handset
 * and tablet-size devices. On handsets, the activity presents a list of items, which when touched,
 * lead to a [UserDetailActivity] representing item details. On tablets, the activity presents
 * the list of items and item details side-by-side using two vertical panes.
 */
class UserListActivity : OnStartDragListener, ActionMode.Callback,
        BaseActivity<UserListIntents, UserListResult, UserListState, UserListEffect, UserListVM>() {

    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var usersAdapter: GenericRecyclerViewAdapter
    private var actionMode: ActionMode? = null
    private var currentFragTag: String = ""
    private var twoPane: Boolean = false

    override fun initialize() {
        viewModel = getViewModel()
        provideFallbackViewState(EmptyState())
    }

    override fun setupUI(isNew: Boolean) {
        setContentView(R.layout.activity_user_list)
        setSupportActionBar(toolbar)
        toolbar.title = title
        setupRecyclerView()
        twoPane = findViewById<View>(R.id.user_detail_container) != null
    }

    override fun onResume() {
        super.onResume()
        if (viewState is EmptyState) {
            viewModel.offer(GetPaginatedUsersIntent(0))
        }
    }

    override fun bindState(successState: UserListState) {
        usersAdapter.setDataList(successState.list, successState.callback)
    }

    override fun bindEffect(effectBundle: UserListEffect) {
        when (effectBundle) {
            is NavigateTo -> {
                val userDetailState = IntentBundleState(twoPane, effectBundle.user)
                if (twoPane) {
                    if (currentFragTag.isNotBlank()) {
                        removeFragment(currentFragTag)
                    }
                    val orderDetailFragment = UserDetailFragment.newInstance(userDetailState)
                    currentFragTag = orderDetailFragment.javaClass.simpleName + effectBundle.user.id
                    addFragment(R.id.user_detail_container, orderDetailFragment, currentFragTag)
                } else {
                    startActivity(UserDetailActivity
                            .getCallingIntent(this@UserListActivity, userDetailState, false))
                }
            }
        }
    }

    override fun toggleLoadingViews(isLoading: Boolean, intent: UserListIntents?) {
        linear_layout_loader.bringToFront()
        linear_layout_loader.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun bindError(errorMessage: String, cause: Throwable, intent: UserListIntents) {
        showErrorSnackBarWithAction(errorMessage, user_list, "Retry",
                View.OnClickListener { viewModel.offer(intent) })
    }

    private fun setupRecyclerView() {
        usersAdapter = object : GenericRecyclerViewAdapter() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenericViewHolder<*> {
                return when (viewType) {
                    SECTION_HEADER -> SectionHeaderViewHolder(layoutInflater
                            .inflate(R.layout.section_header_layout, parent, false))
                    R.layout.empty_view -> EmptyViewHolder(layoutInflater
                            .inflate(R.layout.empty_view, parent, false))
                    R.layout.user_item_layout -> UserViewHolder(layoutInflater
                            .inflate(R.layout.user_item_layout, parent, false))
                    else -> throw IllegalArgumentException("Could not find view of type $viewType")
                }
            }
        }
        usersAdapter.setAreItemsClickable(true)
        usersAdapter.setOnItemLongClickListener(object : OnItemLongClickListener {
            override fun onItemLongClicked(position: Int, itemInfo: ItemInfo<*>, holder: GenericViewHolder<*>): Boolean {
                if (usersAdapter.isSelectionAllowed) {
                    actionMode = startSupportActionMode(this@UserListActivity)
                    toggleItemSelection(position)
                }
                return true
            }
        })
        intentStream = intentStream
                .mergeWith(usersAdapter.itemClickObservable.flatMap {
                    if (actionMode != null) {
                        toggleItemSelection(it.position)
                        Observable.empty()
                    } else {
                        Observable.just(UserClickedIntent(it.itemInfo.data as User))
                    }
                })
                .mergeWith(usersAdapter.itemSwipeObservable
                        .map { itemInfo -> DeleteUsersIntent(listOf((itemInfo.data as User).login)) }
                        .doOnEach { Log.d("DeleteIntent", FIRED) })
        user_list.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        user_list.adapter = usersAdapter
        usersAdapter.setAllowSelection(true)
        //        fastScroller.setRecyclerView(userRecycler);
        intentStream = intentStream.mergeWith(RxRecyclerView.scrollEvents(user_list)
                .map { recyclerViewScrollEvent ->
                    GetPaginatedUsersIntent(
                            if (ScrollEventCalculator.isAtScrollEnd(recyclerViewScrollEvent))
                                viewState.lastId
                            else -1)
                }
                .filter { it.lastId != -1L }
                .throttleLast(200, TimeUnit.MILLISECONDS, Schedulers.computation())
                .debounce(300, TimeUnit.MILLISECONDS, Schedulers.computation())
                .doOnNext { Log.d("NextPageIntent", FIRED) })
        itemTouchHelper = ItemTouchHelper(SimpleItemTouchHelperCallback(usersAdapter))
        itemTouchHelper.attachToRecyclerView(user_list)
    }

    private fun toggleItemSelection(position: Int) {
        usersAdapter.toggleSelection(position)
        val count = usersAdapter.selectedItemCount
        if (count == 0) {
            actionMode?.finish()
        } else {
            actionMode?.title = count.toString()
            actionMode?.invalidate()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.list_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.menu_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setOnCloseListener {
            viewModel.offer(GetPaginatedUsersIntent(viewState.lastId))
            false
        }
        intentStream = intentStream.mergeWith(RxSearchView.queryTextChanges(searchView)
                .filter { charSequence -> charSequence.toString().isNotEmpty() }
                .throttleLast(100, TimeUnit.MILLISECONDS, Schedulers.computation())
                .debounce(200, TimeUnit.MILLISECONDS, Schedulers.computation())
                .map { query -> SearchUsersIntent(query.toString()) }
                .distinctUntilChanged()
                .doOnEach { Log.d("SearchIntent", FIRED) })
        return super.onCreateOptionsMenu(menu)
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        mode.menuInflater.inflate(R.menu.selected_list_menu, menu)
        menu.findItem(R.id.delete_item).setOnMenuItemClickListener {
            viewModel.offer(DeleteUsersIntent(Observable.fromIterable(usersAdapter.selectedItems)
                    .map<String> { itemInfo -> (itemInfo.data as User).login }.toList()
                    .blockingGet()))
            true
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        menu.findItem(R.id.delete_item).setVisible(true).isEnabled = true
        toolbar.visibility = View.GONE
        return true
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean = item.itemId == R.id.delete_item

    override fun onDestroyActionMode(mode: ActionMode) {
        try {
            usersAdapter.clearSelection()
        } catch (e: Exception) {
            Log.e("onDestroyActionMode", e.message, e)
        }
        actionMode = null
        toolbar.visibility = View.VISIBLE
    }

    override fun onStartDrag(viewHolder: androidx.recyclerview.widget.RecyclerView.ViewHolder) = itemTouchHelper.startDrag(viewHolder)

    fun getImageViewAvatar(): ImageView = imageView_avatar

    companion object {
        const val FIRED = "fired!"
    }
}
