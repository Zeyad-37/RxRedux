package com.zeyad.rxredux.core.redux;

import com.zeyad.rxredux.core.BaseEvent;

/**
 * @author by Zeyad Gasser on 4/20/17.
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
