package com.zeyad.rxredux.screens.user.list.events

import com.zeyad.rxredux.core.BaseEvent
import kotlinx.android.parcel.Parcelize

@Parcelize
class GetPaginatedUsersEvent(private val lastId: Long) : BaseEvent<Long> {
    override fun getPayLoad(): Long = lastId
}
