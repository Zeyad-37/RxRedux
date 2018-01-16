package com.zeyad.rxredux.screens.user.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.zeyad.gadapter.GenericRecyclerViewAdapter;
import com.zeyad.rxredux.R;
import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.ErrorMessageFactory;
import com.zeyad.rxredux.screens.BaseFragment;
import com.zeyad.rxredux.screens.user.User;
import com.zeyad.rxredux.screens.user.ViewModelFactory;
import com.zeyad.rxredux.screens.user.list.UserListActivity;
import com.zeyad.rxredux.utils.Utils;
import com.zeyad.usecases.api.DataServiceFactory;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;

import static com.zeyad.rxredux.core.redux.prelollipop.BaseActivity.UI_MODEL;

/**
 * A fragment representing a single Repository detail screen. This fragment is either contained in a
 * {@link UserListActivity} in two-pane mode (on tablets) or a {@link UserDetailActivity} on
 * handsets.
 */
public class UserDetailFragment extends BaseFragment<UserDetailState, UserDetailVM> {
    @BindView(R.id.linear_layout_loader)
    LinearLayout loaderLayout;

    @BindView(R.id.recyclerView_repositories)
    RecyclerView recyclerViewRepositories;

    private GenericRecyclerViewAdapter repositoriesAdapter;

    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String,
            GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target,
                                   boolean isFirstResource) {
            return glideRequestListenerCore();
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                       boolean isFromMemoryCache, boolean isFirstResource) {
            return glideRequestListenerCore();
        }
    };

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the fragment (e.g. upon
     * screen orientation changes).
     */
    public UserDetailFragment() {
    }

    public static UserDetailFragment newInstance(UserDetailState userDetailState) {
        UserDetailFragment userDetailFragment = new UserDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(UI_MODEL, Parcels.wrap(userDetailState));
        userDetailFragment.setArguments(bundle);
        return userDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        postponeEnterTransition();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(
                    TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
        //        setSharedElementReturnTransition(null); // supply the correct element for return transition
    }

    @NonNull
    @Override
    public ErrorMessageFactory errorMessageFactory() {
        return Throwable::getLocalizedMessage;
    }

    @Override
    public void initialize() {
        viewModel = ViewModelProviders.of(this,
                new ViewModelFactory(DataServiceFactory.getInstance())).get(UserDetailVM.class);
    }

    @Override
    public Observable<BaseEvent> events() {
        return Observable.just(new GetReposEvent(viewState.getUser().getLogin()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_detail, container, false);
        ButterKnife.bind(this, rootView);
        setupRecyclerView();
        return rootView;
    }

    private void setupRecyclerView() {
        recyclerViewRepositories.setLayoutManager(new LinearLayoutManager(getContext()));
        repositoriesAdapter = new GenericRecyclerViewAdapter(
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE),
                new ArrayList<>()) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new RepositoryViewHolder(getLayoutInflater().inflate(viewType, parent, false));
            }
        };
        recyclerViewRepositories.setAdapter(repositoriesAdapter);
    }

    @Override
    public void renderSuccessState(UserDetailState userDetailState) {
        repositoriesAdapter.animateTo(userDetailState.getRepos());
        User user = userDetailState.getUser();
        if (userDetailState.isTwoPane()) {
            Utils.runIfNotNull((UserListActivity) getActivity(), activity -> {
                Toolbar appBarLayout = activity.findViewById(R.id.toolbar);
                if (appBarLayout != null) {
                    appBarLayout.setTitle(user.getLogin());
                }
                if (Utils.isNotEmpty(user.getAvatarUrl())) {
                    Glide.with(getContext()).load(user.getAvatarUrl()).dontAnimate().listener(requestListener)
                            .into(activity.imageViewAvatar);
                }
            });
        } else {
            Utils.runIfNotNull((UserDetailActivity) getActivity(), activity -> {
                CollapsingToolbarLayout appBarLayout = activity.collapsingToolbarLayout;
                appBarLayout.setTitle(user.getLogin());
                if (Utils.isNotEmpty(user.getAvatarUrl())) {
                    Glide.with(getContext()).load(user.getAvatarUrl()).dontAnimate().listener(requestListener)
                            .into(activity.imageViewAvatar);
                }
            });
        }
        //        applyPalette();
    }

    boolean glideRequestListenerCore() {
        Utils.runIfNotNull(getActivity(), FragmentActivity::supportStartPostponedEnterTransition);
        return false;
    }

    @Override
    public void toggleViews(boolean toggle) {
        loaderLayout.bringToFront();
        loaderLayout.setVisibility(toggle ? View.VISIBLE : View.GONE);
    }

    @Override
    public void showError(String message) {
        showErrorSnackBar(message, loaderLayout, Snackbar.LENGTH_LONG);
    }

    private void applyPalette() {
        if (Utils.hasM()) {
            UserDetailActivity activity = (UserDetailActivity) getActivity();
            BitmapDrawable drawable = (BitmapDrawable) activity.imageViewAvatar.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Palette.from(bitmap).generate(palette -> activity.findViewById(R.id.coordinator_detail)
                    .setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                        if (v.getHeight() == scrollX) {
                            activity.toolbar.setTitleTextColor(palette.getLightVibrantColor(Color.TRANSPARENT));
                            activity.toolbar.setBackground(
                                    new ColorDrawable(palette.getLightVibrantColor(Color.TRANSPARENT)));
                        } else if (scrollY == 0) {
                            activity.toolbar.setTitleTextColor(0);
                            activity.toolbar.setBackground(null);
                        }
                    }));
        }
    }
}
