package com.zeyad.rxredux.screens.user.list

import android.os.Parcel
import android.os.Parcelable
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.screens.user.User
import io.reactivex.Observable

/**
 * @author by ZIaDo on 1/28/17.
 */
//@Parcel
data class UserListState(val users: List<ItemInfo> = emptyList(),
                         val searchList: List<ItemInfo> = emptyList(),
                         var lastId: Long = 0) : Parcelable {


    constructor(parcel: Parcel) : this(emptyList(), emptyList(), parcel.readLong())

    private constructor(builder: Builder) : this(builder.users, builder.searchList, builder.lastId)

    class Builder {
        var users: List<ItemInfo> = emptyList()
        var searchList: List<ItemInfo> = emptyList()
        var lastId: Long = 0

        fun users(value: List<User>): Builder {
            users = Observable.fromIterable(value)
                    .map { user -> ItemInfo(user, R.layout.user_item_layout).setId(user.id.toLong()) }
                    .toList(value.size).blockingGet()
            return this
        }

        fun searchList(value: List<User>): Builder {
            searchList = Observable.fromIterable(value)
                    .map { user -> ItemInfo(user, R.layout.user_item_layout).setId(user.id.toLong()) }
                    .toList().blockingGet()
            return this
        }

        fun lastId(value: Long): Builder {
            lastId = value
            return this
        }

        fun build(): UserListState {
            return UserListState(this)
        }
    }

    fun isEmpty() = users.isEmpty() && searchList.isEmpty() && lastId < 1

    companion object {
        val CREATOR: Parcelable.Creator<UserListState> = object : Parcelable.Creator<UserListState> {
            override fun createFromParcel(source: Parcel) = UserListState(source)

            override fun newArray(size: Int) = arrayOfNulls<UserListState>(size)
        }

        fun builder(): Builder {
            return Builder()
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) = parcel.writeLong(lastId)

    override fun describeContents() = 0
}
