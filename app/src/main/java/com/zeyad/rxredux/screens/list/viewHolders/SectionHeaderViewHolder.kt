package com.zeyad.rxredux.screens.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericViewHolder
import kotlinx.android.synthetic.main.section_header_layout.view.*

class SectionHeaderViewHolder(itemView: View) : GenericViewHolder<String>(itemView) {
    override fun bindData(data: String, position: Int, isItemSelected: Boolean, isEnabled: Boolean, isExpanded: Boolean) {
        itemView.sectionHeader.text = data
    }
}
