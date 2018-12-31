package com.zeyad.rxredux.screens.user.detail

import android.os.Parcelable
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.screens.user.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserDetailState(val isTwoPane: Boolean = false,
                           val user: User = User(),
                           val repos: List<ItemInfo> = emptyList()) : Parcelable
