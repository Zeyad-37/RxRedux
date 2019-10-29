package com.zeyad.rxredux.screens.list

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.screens.User
import com.zeyad.rxredux.screens.UserDiffCallBack
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

sealed class UserListState : Parcelable {
    abstract val list: List<ItemInfo<User>>
    abstract val lastId: Long
    abstract val callback: DiffUtil.DiffResult
}

@Parcelize
data class EmptyState(override val list: List<ItemInfo<User>> = emptyList(),
                      override val lastId: Long = 1
) : UserListState(), Parcelable {
    @IgnoredOnParcel
    override var callback: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
}

@Parcelize
data class GetState(override val list: List<ItemInfo<User>> = emptyList(),
                    override val lastId: Long = 1
) : UserListState(), Parcelable {
    @IgnoredOnParcel
    override var callback: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))

    constructor(list: List<ItemInfo<User>> = emptyList(), lastId: Long = 1,
                callback: DiffUtil.DiffResult =
                        DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
    ) : this(list, lastId) {
        this.callback = callback
    }
}

sealed class UserListEffect
data class NavigateTo(val user: User) : UserListEffect()

sealed class UserListIntents

data class DeleteUsersIntent(val selectedItemsIds: List<String>) : UserListIntents()

data class GetPaginatedUsersIntent(val lastId: Long) : UserListIntents()

data class SearchUsersIntent(val query: String) : UserListIntents()

data class UserClickedIntent(val user: User) : UserListIntents()

sealed class UserListResult

object EmptyResult : UserListResult()
data class UsersResult(val list: List<User>) : UserListResult()
