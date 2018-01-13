package com.zeyad.rxredux.screens.user.list;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent;

/**
 * @author ZIaDo on 1/9/18.
 */
class ScrollEventCalculator {
    private RecyclerViewScrollEvent recyclerViewScrollEvent;

    public ScrollEventCalculator(RecyclerViewScrollEvent recyclerViewScrollEvent) {
        this.recyclerViewScrollEvent = recyclerViewScrollEvent;
    }

    /**
     * Determine if the scroll event at the end of the recycler view.
     *
     * @return true if at end of linear list recycler view, false otherwise.
     */
    public boolean isAtScrollEnd() {
        RecyclerView.LayoutManager layoutManager = recyclerViewScrollEvent.view().getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;

            int totalItemCount = linearLayoutManager.getItemCount();
            int lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

            return totalItemCount <= (lastVisibleItem + 2);
        } else {
            return false;
        }
    }
}
