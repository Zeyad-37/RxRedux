package com.zeyad.rxredux.screens.user;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.zeyad.gadapter.ItemInfo;

import java.util.List;

/**
 * @author ZIaDo on 12/13/17.
 */

public class UserDiffCallBack extends DiffUtil.Callback {

    List<ItemInfo> oldUsers;
    List<ItemInfo> newUsers;

    public UserDiffCallBack(List<ItemInfo> newUsers, List<ItemInfo> oldUsers) {
        this.newUsers = newUsers;
        this.oldUsers = oldUsers;
    }

    @Override
    public int getOldListSize() {
        return oldUsers.size();
    }

    @Override
    public int getNewListSize() {
        return newUsers.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldUsers.get(oldItemPosition).getId() == newUsers.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldUsers.get(oldItemPosition).<User>getData().equals(newUsers.get(newItemPosition)
                .<User>getData());
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
