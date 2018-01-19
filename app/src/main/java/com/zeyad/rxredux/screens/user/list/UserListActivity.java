package com.zeyad.rxredux.screens.user.list;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jakewharton.rxbinding2.support.v7.widget.RxRecyclerView;
import com.jakewharton.rxbinding2.support.v7.widget.RxSearchView;
import com.zeyad.gadapter.GenericRecyclerViewAdapter;
import com.zeyad.gadapter.ItemInfo;
import com.zeyad.gadapter.OnStartDragListener;
import com.zeyad.gadapter.SimpleItemTouchHelperCallback;
import com.zeyad.rxredux.R;
import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.ErrorMessageFactory;
import com.zeyad.rxredux.screens.BaseActivity;
import com.zeyad.rxredux.screens.user.User;
import com.zeyad.rxredux.screens.user.UserDiffCallBack;
import com.zeyad.rxredux.screens.user.ViewModelFactory;
import com.zeyad.rxredux.screens.user.detail.UserDetailActivity;
import com.zeyad.rxredux.screens.user.detail.UserDetailFragment;
import com.zeyad.rxredux.screens.user.detail.UserDetailState;
import com.zeyad.rxredux.screens.user.list.events.DeleteUsersEvent;
import com.zeyad.rxredux.screens.user.list.events.GetPaginatedUsersEvent;
import com.zeyad.rxredux.screens.user.list.events.SearchUsersEvent;
import com.zeyad.rxredux.screens.user.list.viewHolders.EmptyViewHolder;
import com.zeyad.rxredux.screens.user.list.viewHolders.SectionHeaderViewHolder;
import com.zeyad.rxredux.screens.user.list.viewHolders.UserViewHolder;
import com.zeyad.rxredux.utils.Utils;
import com.zeyad.usecases.api.DataServiceFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.subjects.PublishSubject;

import static com.zeyad.gadapter.ItemInfo.SECTION_HEADER;
import static java.util.Collections.singletonList;

/**
 * An activity representing a list of Repos. This activity has different presentations for handset
 * and tablet-size devices. On handsets, the activity presents a list of items, which when touched,
 * lead to a {@link UserDetailActivity} representing item details. On tablets, the activity presents
 * the list of items and item details side-by-side using two vertical panes.
 */
public class UserListActivity extends BaseActivity<UserListState, UserListVM> implements
        OnStartDragListener, ActionMode.Callback {
    public static final String FIRED = "fired!";

    @BindView(R.id.imageView_avatar)
    public ImageView imageViewAvatar;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;

    @BindView(R.id.user_list)
    RecyclerView userRecycler;

//    @BindView(R.id.fastscroll)
//    FastScroller fastScroller;

    private ItemTouchHelper itemTouchHelper;
    private GenericRecyclerViewAdapter usersAdapter;
    private ActionMode actionMode;
    private String currentFragTag;
    private boolean twoPane;

    private PublishSubject<BaseEvent> postOnResumeEvents = PublishSubject.create();
    private Observable<BaseEvent> eventObservable;

    public static Intent getCallingIntent(Context context) {
        return new Intent(context, UserListActivity.class);
    }

    @NonNull
    @Override
    public ErrorMessageFactory errorMessageFactory() {
        return Throwable::getLocalizedMessage;
    }

    @Override
    public void initialize() {
        eventObservable = Observable.empty();
        viewModel = ViewModelProviders.of(this,
                new ViewModelFactory(DataServiceFactory.getInstance())).get(UserListVM.class);
        if (viewState == null) {
            eventObservable = Single.<BaseEvent>just(new GetPaginatedUsersEvent(0))
                    .doOnSuccess(event -> Log.d("GetPaginatedUsersEvent", FIRED)).toObservable();
        }
    }

    @Override
    public void setupUI(boolean isNew) {
        setContentView(R.layout.activity_user_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        setupRecyclerView();
        twoPane = findViewById(R.id.user_detail_container) != null;
    }

    @Override
    public Observable<BaseEvent> events() {
        return eventObservable.mergeWith(postOnResumeEvents());
    }

    private Observable<BaseEvent> postOnResumeEvents() {
        return postOnResumeEvents;
    }

    @Override
    public void renderSuccessState(UserListState state) {
        List<ItemInfo> users = state.getUsers();
        List<ItemInfo> searchList = state.getSearchList();
        if (Utils.isNotEmpty(searchList)) {
//            usersAdapter.animateTo(searchList);
            usersAdapter.setDataList(searchList,
                    DiffUtil.calculateDiff(new UserDiffCallBack(searchList,
                            usersAdapter.getAdapterData())));
        } else if (Utils.isNotEmpty(users)) {
//            usersAdapter.animateTo(users);
            usersAdapter.setDataList(users,
                    DiffUtil.calculateDiff(new UserDiffCallBack(users, usersAdapter.getDataList())));
        }
    }

    @Override
    public void toggleViews(boolean toggle) {
        loaderLayout.bringToFront();
        loaderLayout.setVisibility(toggle ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, userRecycler, Snackbar.LENGTH_LONG);
    }

    private void setupRecyclerView() {
        usersAdapter = new GenericRecyclerViewAdapter(
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                switch (viewType) {
                    case SECTION_HEADER:
                        return new SectionHeaderViewHolder(getLayoutInflater()
                                .inflate(R.layout.section_header_layout, parent, false));
                    case R.layout.empty_view:
                        return new EmptyViewHolder(getLayoutInflater()
                                .inflate(R.layout.empty_view, parent, false));
                    case R.layout.user_item_layout:
                        return new UserViewHolder(getLayoutInflater()
                                .inflate(R.layout.user_item_layout, parent, false));
                    default:
                        throw new IllegalArgumentException("Could not find view of type " + viewType);
                }
            }
        };
        usersAdapter.setAreItemsClickable(true);
        usersAdapter.setOnItemClickListener((position, itemInfo, holder) -> {
            if (actionMode != null) {
                toggleSelection(position);
            } else if (itemInfo.getData() instanceof User) {
                User userModel = itemInfo.getData();
                UserDetailState userDetailState = UserDetailState.builder().setUser(userModel).setIsTwoPane(twoPane)
                        .build();
                Pair<View, String> pair = null;
                Pair<View, String> secondPair = null;
                if (Utils.hasLollipop()) {
                    UserViewHolder userViewHolder = (UserViewHolder) holder;
                    ImageView avatar = userViewHolder.getAvatar();
                    pair = Pair.create(avatar, avatar.getTransitionName());
                    TextView textViewTitle = userViewHolder.getTextViewTitle();
                    secondPair = Pair.create(textViewTitle, textViewTitle.getTransitionName());
                }
                if (twoPane) {
                    if (Utils.isNotEmpty(currentFragTag)) {
                        removeFragment(currentFragTag);
                    }
                    UserDetailFragment orderDetailFragment = UserDetailFragment.newInstance(userDetailState);
                    currentFragTag = orderDetailFragment.getClass().getSimpleName() + userModel.getId();
                    addFragment(R.id.user_detail_container, orderDetailFragment, currentFragTag,
                            pair, secondPair);
                } else {
                    if (Utils.hasLollipop()) {
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this, pair,
                                secondPair);
                        navigator.navigateTo(this, UserDetailActivity.getCallingIntent(this, userDetailState),
                                options);
                    } else {
                        navigator.navigateTo(this, UserDetailActivity.getCallingIntent(this, userDetailState));
                    }
                }
            }
        });
        usersAdapter.setOnItemLongClickListener((position, itemInfo, holder) -> {
            if (usersAdapter.isSelectionAllowed()) {
                actionMode = startSupportActionMode(UserListActivity.this);
                toggleSelection(position);
            }
            return true;
        });
        eventObservable = eventObservable.mergeWith(usersAdapter.getItemSwipeObservable()
                .map(itemInfo ->
                        new DeleteUsersEvent(singletonList(((User) itemInfo.getData()).getLogin())))
                .doOnEach(notification -> Log.d("DeleteEvent", FIRED)));
        userRecycler.setLayoutManager(new LinearLayoutManager(this));
        userRecycler.setAdapter(usersAdapter);
        usersAdapter.setAllowSelection(true);
//        fastScroller.setRecyclerView(userRecycler);
        eventObservable = eventObservable.mergeWith(RxRecyclerView.scrollEvents(userRecycler)
                .map(recyclerViewScrollEvent -> new GetPaginatedUsersEvent(ScrollEventCalculator
                        .isAtScrollEnd(recyclerViewScrollEvent) ? viewState.getLastId() : -1))
                .filter(usersNextPageEvent -> usersNextPageEvent.getPayLoad() != -1)
                .throttleLast(200, TimeUnit.MILLISECONDS)
                .debounce(300, TimeUnit.MILLISECONDS)
                .doOnNext(searchUsersEvent -> Log.d("NextPageEvent", FIRED)));
        itemTouchHelper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(usersAdapter));
        itemTouchHelper.attachToRecyclerView(userRecycler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnCloseListener(() -> {
            postOnResumeEvents.onNext(new GetPaginatedUsersEvent(viewState == null ? -1 : viewState.getLastId()));
            return false;
        });
        eventObservable = eventObservable.mergeWith(RxSearchView.queryTextChanges(searchView)
                .filter(charSequence -> !charSequence.toString().isEmpty())
                .map(query -> new SearchUsersEvent(query.toString()))
                .distinctUntilChanged()
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .debounce(200, TimeUnit.MILLISECONDS)
                .doOnEach(searchUsersEvent -> Log.d("SearchEvent", FIRED)));
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Toggle the selection viewState of an item.
     * <p>
     * <p>If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection viewState
     */
    private void toggleSelection(int position) {
        usersAdapter.toggleSelection(position);
        int count = usersAdapter.getSelectedItemCount();
        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.selected_list_menu, menu);
        menu.findItem(R.id.delete_item).setOnMenuItemClickListener(menuItem -> {
            postOnResumeEvents.onNext(new DeleteUsersEvent(Observable.fromIterable(usersAdapter.getSelectedItems())
                    .map(itemInfo -> itemInfo.<User>getData().getLogin()).toList()
                    .blockingGet()));
            return true;
        });
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        menu.findItem(R.id.delete_item).setVisible(true).setEnabled(true);
        toolbar.setVisibility(View.GONE);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return item.getItemId() == R.id.delete_item;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        try {
            usersAdapter.clearSelection();
        } catch (Exception e) {
            Log.e("onDestroyActionMode", e.getMessage(), e);
        }
        actionMode = null;
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        itemTouchHelper.startDrag(viewHolder);
    }
}
