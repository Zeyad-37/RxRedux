package com.zeyad.rxredux.screens.user.detail

import android.os.Parcel
import android.os.Parcelable
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.R
import com.zeyad.rxredux.screens.user.User
import io.reactivex.Observable

/**
 * @author zeyad on 1/25/17.
 */
data class UserDetailState(val isTwoPane: Boolean = false,
                           val user: User? = null,
                           val repos: List<ItemInfo> = emptyList()) : Parcelable {

    constructor(builder: Builder) : this(builder.isTwoPane, builder.user, builder.repos)

    constructor(parcel: Parcel) : this(
            parcel.readByte() != 0.toByte(),
            parcel.readParcelable(User::class.java.classLoader),
            emptyList())


    class Builder internal constructor() {
        internal var repos: List<ItemInfo> = emptyList()
        internal var user: User? = null
        internal var isTwoPane: Boolean = false

        fun setRepos(value: List<Repository>): Builder {
            repos = Observable.fromIterable(value)
                    .map { repository -> ItemInfo(repository, R.layout.repo_item_layout) }
                    .toList(value.size).blockingGet()
            //            repos = value;
            return this
        }

        fun setIsTwoPane(value: Boolean): Builder {
            isTwoPane = value
            return this
        }

        fun setUser(value: User): Builder {
            user = value
            return this
        }

        fun build(): UserDetailState = UserDetailState(this)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (isTwoPane) 1 else 0)
        parcel.writeParcelable(user, flags)
    }

    override fun describeContents() = 0

    companion object CREATOR : Parcelable.Creator<UserDetailState> {
        override fun createFromParcel(parcel: Parcel) = UserDetailState(parcel)

        override fun newArray(size: Int) = arrayOfNulls<UserDetailState>(size)

        fun builder(): Builder {
            return Builder()
        }
    }
}
