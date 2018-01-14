package com.zeyad.rxredux.screens.user.list.events;

import com.zeyad.rxredux.core.redux.BaseEvent;

/**
 * @author by ZIaDo on 4/19/17.
 */
public class GetPaginatedUsersEvent implements BaseEvent<Long> {

    private final long lastId;

    public GetPaginatedUsersEvent(long lastId) {
        this.lastId = lastId;
    }

    @Override
    public Long getPayLoad() {
        return lastId;
    }
}
