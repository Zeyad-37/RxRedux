package com.zeyad.rxredux.screens.user.detail;

import com.zeyad.rxredux.core.redux.BaseEvent;

/**
 * @author by ZIaDo on 4/22/17.
 */
class GetReposEvent implements BaseEvent<String> {
    private final String login;

    GetReposEvent(String login) {
        this.login = login;
    }

    @Override
    public String getPayLoad() {
        return login;
    }
}
