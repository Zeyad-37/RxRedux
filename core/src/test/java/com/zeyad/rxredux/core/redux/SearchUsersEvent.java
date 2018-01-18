package com.zeyad.rxredux.core.redux;

/**
 * @author by ZIaDo on 4/20/17.
 */
public class SearchUsersEvent implements BaseEvent {

    private final String query;

    public SearchUsersEvent(String s) {
        query = s;
    }

    @Override
    public String getPayLoad() {
        return query;
    }
}
