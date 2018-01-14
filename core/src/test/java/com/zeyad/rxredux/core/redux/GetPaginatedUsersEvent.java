package com.zeyad.rxredux.core.redux;

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
