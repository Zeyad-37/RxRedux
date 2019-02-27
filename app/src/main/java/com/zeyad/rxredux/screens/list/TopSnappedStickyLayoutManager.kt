package com.zeyad.rxredux.screens.list

import android.content.Context
import com.zeyad.gadapter.stickyheaders.StickyLayoutManager
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler

internal class TopSnappedStickyLayoutManager(context: Context, headerHandler: StickyHeaderHandler) :
        StickyLayoutManager(context, headerHandler) {

    override fun scrollToPosition(position: Int) {
        super.scrollToPositionWithOffset(position, 0)
    }
}
