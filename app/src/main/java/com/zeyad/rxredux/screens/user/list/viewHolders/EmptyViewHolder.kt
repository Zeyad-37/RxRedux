package com.zeyad.rxredux.screens.user.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericRecyclerViewAdapter

class EmptyViewHolder(itemView: View) : GenericRecyclerViewAdapter.GenericViewHolder<Any>(itemView) {
    override fun bindData(data: Any, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {}
}
