package com.zeyad.rxredux.screens.detail

import android.content.Intent
import android.os.Parcelable
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.screens.User
import kotlinx.android.parcel.Parcelize

sealed class UserDetailState : Parcelable

@Parcelize
data class IntentBundleState(val isTwoPane: Boolean = false,
                             val user: User = User()) : UserDetailState()

@Parcelize
data class FullDetailState(val isTwoPane: Boolean = false,
                           val user: User = User(),
                           val repos: List<ItemInfo> = emptyList()) : UserDetailState()

sealed class UserDetailEffect

data class NavigateFromDetail(val intent: Intent, val shouldFinish: Boolean) : UserDetailEffect()

sealed class UserDetailEvents

internal class GetReposEvent(val login: String) : UserDetailEvents()

class NavigateToEvent(val intent: Intent) : UserDetailEvents()

sealed class UserDetailResult

data class ListRepository(val repos: List<Repository>) : UserDetailResult()