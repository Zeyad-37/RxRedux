package com.zeyad.rxredux.core.redux;

import com.zeyad.rxredux.core.BaseEvent;

import java.util.List;

/**
 * @author by Zeyad Gasser on 3/27/17.
 */
public final class DeleteUsersEvent implements BaseEvent<List<String>> {

    private final List<String> selectedItemsIds;

    public DeleteUsersEvent(List<String> selectedItemsIds) {
        this.selectedItemsIds = selectedItemsIds;
    }

    @Override
    public List<String> getPayLoad() {
        return selectedItemsIds;
    }
}
