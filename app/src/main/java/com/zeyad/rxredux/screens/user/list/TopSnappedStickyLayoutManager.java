package com.zeyad.rxredux.screens.user.list;

import com.zeyad.gadapter.stickyheaders.StickyLayoutManager;
import com.zeyad.gadapter.stickyheaders.exposed.StickyHeaderHandler;

import android.content.Context;

final class TopSnappedStickyLayoutManager extends StickyLayoutManager {

    TopSnappedStickyLayoutManager(Context context, StickyHeaderHandler headerHandler) {
        super(context, headerHandler);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPositionWithOffset(position, 0);
    }
}
