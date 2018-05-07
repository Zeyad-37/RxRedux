package com.zeyad.rxredux.screens.user.list.events

import com.zeyad.rxredux.core.BaseEvent

/**
 * @author by ZIaDo on 4/20/17.
 */
class SearchUsersEvent(private val query: String) : BaseEvent<String> {

    override fun getPayLoad(): String {
        return query
    }
}
