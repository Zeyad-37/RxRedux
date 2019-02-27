package com.zeyad.rxredux.screens.detail

import android.view.View
import com.zeyad.gadapter.GenericViewHolder

import kotlinx.android.synthetic.main.repo_item_layout.view.*

internal class RepositoryViewHolder(itemView: View) : GenericViewHolder<Repository>(itemView) {
    override fun bindData(repository: Repository, position: Int, isItemSelected: Boolean, isEnabled: Boolean, isExpanded: Boolean) {
        itemView.textView_repo_title.text = repository.name
    }
}
