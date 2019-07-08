package com.zeyad.rxredux.screens.list

import android.os.Parcelable
import android.support.v7.util.DiffUtil
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.annotations.LeafVertex
import com.zeyad.rxredux.annotations.RootVertex
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.screens.User
import com.zeyad.rxredux.screens.UserDiffCallBack
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

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
    override var callback: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
}

@Parcelize
data class GetState(override val list: List<ItemInfo> = emptyList(),
                    override val lastId: Long = 1
) : UserListState(), Parcelable {
    @IgnoredOnParcel
    override var callback: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))

    constructor(list: List<ItemInfo> = emptyList(), lastId: Long = 1,
                callback: DiffUtil.DiffResult =
                        DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
    ) : this(list, lastId) {
        this.callback = callback
    }
}

sealed class UserListEffect
data class NavigateTo(val user: User) : UserListEffect()

sealed class UserListEvents

data class DeleteUsersEvent(val selectedItemsIds: List<String>) : UserListEvents()

data class GetPaginatedUsersEvent(val lastId: Long) : UserListEvents()

data class SearchUsersEvent(val query: String) : UserListEvents()

data class UserClickedEvent(val user: User) : UserListEvents()

sealed class UserListResult

object EmptyResult : UserListResult()
data class UsersResult(val list: List<User>) : UserListResult()
