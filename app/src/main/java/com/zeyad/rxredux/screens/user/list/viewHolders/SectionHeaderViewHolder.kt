package com.zeyad.rxredux.screens.user.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericRecyclerViewAdapter
import kotlinx.android.synthetic.main.section_header_layout.view.*

class SectionHeaderViewHolder(itemView: View) : GenericRecyclerViewAdapter.GenericViewHolder<String>(itemView) {
    override fun bindData(title: String, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {
        itemView.sectionHeader.text = title
    }
}
