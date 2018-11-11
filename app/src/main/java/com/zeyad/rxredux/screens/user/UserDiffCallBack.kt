package com.zeyad.rxredux.screens.user

import android.support.v7.util.DiffUtil

import com.zeyad.gadapter.ItemInfo

class UserDiffCallBack(private var newUsers: List<ItemInfo>,
                       private var oldUsers: List<ItemInfo>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldUsers.size
    }

    override fun getNewListSize(): Int {
        return newUsers.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUsers[oldItemPosition].id == newUsers[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUsers[oldItemPosition].getData<User>() == newUsers[newItemPosition]
                .getData<User>()
    }

    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        //you can return particular field for changed item.
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}
