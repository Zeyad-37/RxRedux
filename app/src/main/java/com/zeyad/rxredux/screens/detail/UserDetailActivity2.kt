package com.zeyad.rxredux.screens.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Pair
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import com.zeyad.rxredux.R
import com.zeyad.rxredux.core.view.P_MODEL
import kotlinx.android.synthetic.main.activity_user_detail.*

/**
 * An activity representing a single Repository detail screen. This activity is only used narrow
 * width devices. On tablet-size devices, item details are presented side-by-side with a list of
 * items in a [UserListActivity].
 */
class UserDetailActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_detail)
        setSupportActionBar(detail_toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.title = ""
        }
        if (savedInstanceState == null) {
            val fragment = UserDetailFragment2.newInstance(intent.getParcelableExtra(P_MODEL))
            addFragment(R.id.user_detail_container, fragment, fragment.tag, null)
        }
    }

    private fun addFragment(containerViewId: Int, fragment: Fragment, currentFragTag: String?,
                            sharedElements: List<Pair<View, String>>?) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (sharedElements != null) {
            for (pair in sharedElements) {
                fragmentTransaction.addSharedElement(pair.first, pair.second)
            }
        }
        if (currentFragTag == null || currentFragTag.isEmpty()) {
            fragmentTransaction.addToBackStack(fragment.tag)
        } else {
            fragmentTransaction.addToBackStack(currentFragTag)
        }
        fragmentTransaction.add(containerViewId, fragment, fragment.tag).commit()
    }

    override fun onBackPressed() {
        //        navigateUpTo(new Intent(this, UserListActivity.class));
        supportFinishAfterTransition() // exit animation
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun getImageViewAvatar(): ImageView = imageView_avatar
    fun getToolbar(): Toolbar = detail_toolbar
    fun getCollapsingToolbarLayout(): CollapsingToolbarLayout = toolbar_layout

    companion object {

        fun getCallingIntent(context: Context, userDetailModel: UserDetailState): Intent {
            return Intent(context, UserDetailActivity2::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(P_MODEL, userDetailModel)
        }
    }
}
