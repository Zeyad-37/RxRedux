package com.zeyad.rxredux.screens.user.detail;

import com.zeyad.rxredux.core.redux.BaseEvent;

/**
 * @author by ZIaDo on 4/22/17.
 */
class GetReposEvent implements BaseEvent {
    private final String login;

    GetReposEvent(String login) {
        this.login = login;
    }

    String getLogin() {
        return login;
    }
}
