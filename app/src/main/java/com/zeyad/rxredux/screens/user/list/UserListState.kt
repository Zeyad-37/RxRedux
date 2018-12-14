package com.zeyad.rxredux.screens.user.list

import android.os.Parcel
import android.os.Parcelable
import android.support.v7.util.DiffUtil
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.screens.user.UserDiffCallBack

sealed class UserListState : Parcelable {
    abstract val list: List<ItemInfo>
    abstract val lastId: Long
    abstract val callback: DiffUtil.DiffResult
}

data class EmptyState(override val list: List<ItemInfo> = emptyList(),
                      override val lastId: Long = 1,
                      override val callback: DiffUtil.DiffResult =
                              DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
) : UserListState(), Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.createTypedArrayList(ItemInfo.CREATOR),
            parcel.readLong(),
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf<ItemInfo>(), mutableListOf<ItemInfo>())))

    override fun writeToParcel(parcel: Parcel, flags: Int) = Unit

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<EmptyState> {
        override fun createFromParcel(parcel: Parcel): EmptyState = EmptyState(parcel)

        override fun newArray(size: Int): Array<EmptyState?> = arrayOfNulls(size)
    }
}

data class GetState(override val list: List<ItemInfo> = emptyList(),
                    override val lastId: Long = 1,
                    override val callback: DiffUtil.DiffResult =
                            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf(), mutableListOf()))
) : UserListState(), Parcelable {

    constructor(parcel: Parcel) : this(emptyList(),
            parcel.readLong(),
            DiffUtil.calculateDiff(UserDiffCallBack(mutableListOf<ItemInfo>(), mutableListOf<ItemInfo>())))

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(lastId)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<GetState> {
        override fun createFromParcel(parcel: Parcel) = GetState(parcel)

        override fun newArray(size: Int) = arrayOfNulls<GetState?>(size)
    }
}
