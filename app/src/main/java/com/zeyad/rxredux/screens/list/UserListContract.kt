package com.zeyad.rxredux.screens.list

import android.os.Parcelable
import android.support.v7.util.DiffUtil
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.screens.User
import com.zeyad.rxredux.screens.UserDiffCallBack
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

sealed class UserListState : Parcelable {
    abstract val list: List<ItemInfo>
    abstract val lastId: Long
    abstract val callback: DiffUtil.DiffResult
}

@Parcelize
data class EmptyState(override val list: List<ItemInfo> = emptyList(),
                      override val lastId: Long = 1
) : UserListState(), Parcelable {
    @IgnoredOnParcel
    override var callback: @RawValue DiffUtil.DiffResult =
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
}

@Parcelize
data class GetState(override val list: List<ItemInfo> = emptyList(),
                    override val lastId: Long = 1
) : UserListState(), Parcelable {
    @IgnoredOnParcel
    override var callback: @RawValue DiffUtil.DiffResult =
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))

    constructor(list: List<ItemInfo> = emptyList(), lastId: Long = 1,
                callback: @RawValue DiffUtil.DiffResult =
                        DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
    ) : this(list, lastId) {
        this.callback = callback
    }
}

class UserListEffect

sealed class UserListEvents<T> : BaseEvent<T>

data class DeleteUsersEvent(private val selectedItemsIds: List<String>) : UserListEvents<List<String>>() {
    override fun getPayLoad(): List<String> = selectedItemsIds
}

data class GetPaginatedUsersEvent(private val lastId: Long) : UserListEvents<Long>() {
    override fun getPayLoad(): Long = lastId
}

data class SearchUsersEvent(private val query: String) : UserListEvents<String>() {
    override fun getPayLoad(): String = query
}

sealed class UserListResult

data class UsersResult(val list: List<User>) : UserListResult()
