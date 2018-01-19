package com.zeyad.rxredux.screens.user.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.zeyad.rxredux.R;
import com.zeyad.rxredux.screens.user.list.UserListActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.zeyad.rxredux.core.redux.BaseView.UI_MODEL;

/**
 * An activity representing a single Repository detail screen. This activity is only used narrow
 * width devices. On tablet-size devices, item details are presented side-by-side with a list of
 * items in a {@link UserListActivity}.
 */
public class UserDetailActivity extends AppCompatActivity {
    @BindView(R.id.detail_toolbar)
    Toolbar toolbar;

    @BindView(R.id.imageView_avatar)
    ImageView imageViewAvatar;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;

    Unbinder unbinder;

    public static Intent getCallingIntent(Context context, UserDetailState userDetailModel) {
        return new Intent(context, UserDetailActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(UI_MODEL, userDetailModel);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        unbinder = ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }
        if (savedInstanceState == null) {
            addFragment(R.id.user_detail_container,
                    UserDetailFragment.newInstance(getIntent().getParcelableExtra(UI_MODEL)),
                    "", null);
        }
    }

    private void addFragment(int containerViewId, Fragment fragment, String currentFragTag,
                             List<Pair<View, String>> sharedElements) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (sharedElements != null) {
            for (Pair<View, String> pair : sharedElements) {
                fragmentTransaction.addSharedElement(pair.first, pair.second);
            }
        }
        if (currentFragTag == null || currentFragTag.isEmpty()) {
            fragmentTransaction.addToBackStack(fragment.getTag());
        } else {
            fragmentTransaction.addToBackStack(currentFragTag);
        }
        fragmentTransaction.add(containerViewId, fragment, fragment.getTag()).commit();
    }

    @Override
    public void onBackPressed() {
        //        navigateUpTo(new Intent(this, UserListActivity.class));
        supportFinishAfterTransition(); // exit animation
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
