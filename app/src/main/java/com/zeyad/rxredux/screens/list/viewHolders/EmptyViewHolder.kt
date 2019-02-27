package com.zeyad.rxredux.screens.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericViewHolder

class EmptyViewHolder(itemView: View) : GenericViewHolder<Any>(itemView) {
    override fun bindData(data: Any, position: Int, isItemSelected: Boolean, isEnabled: Boolean, isExpanded: Boolean) = Unit
}
