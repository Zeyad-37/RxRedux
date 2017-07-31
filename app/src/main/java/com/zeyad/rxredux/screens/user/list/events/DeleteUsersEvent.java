package com.zeyad.rxredux.screens.user.list.events;

import java.util.List;

import com.zeyad.rxredux.core.redux.BaseEvent;

/** @author by ZIaDo on 3/27/17. */
public final class DeleteUsersEvent implements BaseEvent {

    private final List<String> selectedItemsIds;

    public DeleteUsersEvent(List<String> selectedItemsIds) {
        this.selectedItemsIds = selectedItemsIds;
    }

    public List<String> getSelectedItemsIds() {
        return selectedItemsIds;
    }
}
