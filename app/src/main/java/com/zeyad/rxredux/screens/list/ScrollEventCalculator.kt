package com.zeyad.rxredux.screens.list

import com.jakewharton.rxbinding2.support.v7.widget.RecyclerViewScrollEvent

internal object ScrollEventCalculator {

    /**
     * Determine if the scroll event at the end of the recycler view.
     *
     * @return true if at end of linear list recycler view, false otherwise.
     */
    fun isAtScrollEnd(recyclerViewScrollEvent: RecyclerViewScrollEvent): Boolean {
        val layoutManager = recyclerViewScrollEvent.view().layoutManager
        return when (layoutManager) {
            is androidx.recyclerview.widget.LinearLayoutManager -> layoutManager.itemCount <= layoutManager.findLastVisibleItemPosition() + 2
            else -> false
        }
    }
}
