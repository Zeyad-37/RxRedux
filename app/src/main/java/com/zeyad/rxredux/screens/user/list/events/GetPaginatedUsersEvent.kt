package com.zeyad.rxredux.screens.user.list.events

import com.zeyad.rxredux.core.BaseEvent

/**
 * @author by ZIaDo on 4/19/17.
 */
class GetPaginatedUsersEvent(private val lastId: Long) : BaseEvent<Long> {

    override fun getPayLoad(): Long {
        return lastId
    }
}
