package com.zeyad.rxredux.screens.user.detail

import com.zeyad.rxredux.core.BaseEvent

internal class GetReposEvent(private val login: String) : BaseEvent<String> {

    override fun getPayLoad(): String = login
}
