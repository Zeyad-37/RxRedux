package com.zeyad.rxredux.screens.user.list.events;

import com.zeyad.rxredux.core.redux.BaseEvent;

/** @author by ZIaDo on 4/20/17. */
public class SearchUsersEvent implements BaseEvent {

    private final String query;

    public SearchUsersEvent(String s) {
        query = s;
    }

    public String getQuery() {
        return query;
    }
}
