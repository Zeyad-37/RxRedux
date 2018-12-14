package com.zeyad.rxredux.screens.user.list.events

import com.zeyad.rxredux.core.BaseEvent
import kotlinx.android.parcel.Parcelize

@Parcelize
class SearchUsersEvent(private val query: String) : BaseEvent<String> {
    override fun getPayLoad(): String = query
}
