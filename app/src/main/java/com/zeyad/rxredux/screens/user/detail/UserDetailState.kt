package com.zeyad.rxredux.screens.user.detail

import android.os.Parcelable
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.screens.user.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetailState(val isTwoPane: Boolean = false,
                           val user: User = User(),
                           var repos: List<ItemInfo> = emptyList()) : Parcelable {

    constructor(builder: Builder) : this(builder.isTwoPane, builder.user, builder.repos)

    class Builder internal constructor() {
        internal var repos: List<ItemInfo> = emptyList()
        internal var user: User = User()
        internal var isTwoPane: Boolean = false

        fun setRepos(value: List<ItemInfo>): Builder {
            repos = value
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

    companion object {
        fun builder(): Builder {
            return Builder()
        }
    }
}
