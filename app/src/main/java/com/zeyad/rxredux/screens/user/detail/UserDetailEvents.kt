package com.zeyad.rxredux.screens.user.detail

import android.content.Intent
import com.zeyad.rxredux.core.BaseEvent

sealed class UserDetailEvents<T> : BaseEvent<T>

internal class GetReposEvent(private val login: String) : UserDetailEvents<String>() {
    override fun getPayLoad(): String = login
}

class NavigateToEvent(private val intent: Intent) : UserDetailEvents<Intent>() {
    override fun getPayLoad() = intent
}
