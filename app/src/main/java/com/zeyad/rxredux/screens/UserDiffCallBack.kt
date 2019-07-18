package com.zeyad.rxredux.screens

import androidx.recyclerview.widget.DiffUtil

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

}
