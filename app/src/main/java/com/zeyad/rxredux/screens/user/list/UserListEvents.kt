package com.zeyad.rxredux.screens.user.list

import com.zeyad.rxredux.core.BaseEvent
import kotlinx.android.parcel.Parcelize

sealed class UserListEvents<T> : BaseEvent<T>

@Parcelize
data class DeleteUsersEvent(private val selectedItemsIds: List<String>) : UserListEvents<List<String>>() {
    override fun getPayLoad(): List<String> = selectedItemsIds
}

@Parcelize
data class GetPaginatedUsersEvent(private val lastId: Long) : UserListEvents<Long>() {
    override fun getPayLoad(): Long = lastId
}

@Parcelize
data class SearchUsersEvent(private val query: String) : UserListEvents<String>() {
    override fun getPayLoad(): String = query
}