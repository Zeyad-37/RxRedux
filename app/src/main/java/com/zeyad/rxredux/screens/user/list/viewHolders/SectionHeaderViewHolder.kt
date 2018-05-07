package com.zeyad.rxredux.screens.user.list.viewHolders

import android.view.View
import com.zeyad.gadapter.GenericRecyclerViewAdapter
import kotlinx.android.synthetic.main.section_header_layout.view.*

/**
 * @author by ZIaDo on 7/18/17.
 */

class SectionHeaderViewHolder(itemView: View) : GenericRecyclerViewAdapter.GenericViewHolder<String>(itemView) {

    override fun bindData(title: String, isItemSelected: Boolean, position: Int, isEnabled: Boolean) {
        if (title.isNotEmpty()) {
            itemView.sectionHeader.text = title
        }
    }
}
