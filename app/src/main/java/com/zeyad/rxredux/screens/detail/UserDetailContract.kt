package com.zeyad.rxredux.screens.detail

import android.content.Intent
import android.os.Parcelable
import com.zeyad.gadapter.ItemInfo
import com.zeyad.rxredux.screens.User
import kotlinx.android.parcel.Parcelize

sealed class UserDetailState : Parcelable

@Parcelize
data class IntentBundleState(val user: User = User()) : UserDetailState()

@Parcelize
data class FullDetailState(val user: User = User(),
                           val repos: List<ItemInfo<Repository>> = emptyList()) : UserDetailState()

sealed class UserDetailEffect

data class NavigateFromDetail(val intent: Intent, val shouldFinish: Boolean) : UserDetailEffect()

sealed class UserDetailIntents

internal class GetReposIntent(val login: String) : UserDetailIntents()

class NavigateToIntent(val intent: Intent) : UserDetailIntents()

sealed class UserDetailResult

data class ListRepository(val repos: List<Repository>) : UserDetailResult()