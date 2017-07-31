package com.zeyad.rxredux.screens.user.list.viewHolders;

import com.zeyad.gadapter.GenericRecyclerViewAdapter;

import android.view.View;

/** @author zeyad on 11/29/16. */
public class EmptyViewHolder extends GenericRecyclerViewAdapter.ViewHolder {

    public EmptyViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void bindData(Object data, boolean isItemSelected, int position, boolean isEnabled) {
    }
}
