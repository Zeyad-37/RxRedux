package com.zeyad.rxredux.core.redux;

import com.zeyad.rxredux.core.BaseEvent;

/**
 * @author by Zeyad Gasser on 4/19/17.
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
