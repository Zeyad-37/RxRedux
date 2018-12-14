package com.zeyad.rxredux.screens.user.detail

import android.view.View
import com.zeyad.gadapter.GenericRecyclerViewAdapter.GenericViewHolder
import kotlinx.android.synthetic.main.repo_item_layout.view.*

internal class RepositoryViewHolder(itemView: View) : GenericViewHolder<Repository>(itemView) {

    override fun bindData(repository: Repository, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {
        itemView.textView_repo_title.text = repository.name
    }
}
