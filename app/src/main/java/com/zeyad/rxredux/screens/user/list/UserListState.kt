package com.zeyad.rxredux.screens.user.list

import android.os.Parcelable
import android.support.v7.util.DiffUtil
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.screens.user.UserDiffCallBack
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

sealed class UserListState : Parcelable {
    abstract val list: List<ItemInfo>
    abstract val lastId: Long
    abstract val callback: DiffUtil.DiffResult
}

@Parcelize
data class EmptyState(override val list: List<ItemInfo> = emptyList(),
                      override val lastId: Long = 1,
                      override val callback: @RawValue DiffUtil.DiffResult =
                              DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
) : UserListState(), Parcelable

@Parcelize
data class GetState(override val list: List<ItemInfo> = emptyList(),
                    override val lastId: Long = 1,
                    override val callback: @RawValue DiffUtil.DiffResult =
                            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
) : UserListState(), Parcelable
