package com.zeyad.rxredux.screens.user.detail

import android.view.View
import com.zeyad.gadapter.GenericRecyclerViewAdapter
import kotlinx.android.synthetic.main.repo_item_layout.view.*

/**
 * @author zeyad on 1/12/17.
 */
internal class RepositoryViewHolder(itemView: View) : GenericRecyclerViewAdapter.GenericViewHolder<Repository>(itemView) {

    override fun bindData(repository: Repository, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {
        if (repository.name!!.isNotEmpty()) {
            itemView.textView_repo_title!!.text = repository.name
        }
    }
}
