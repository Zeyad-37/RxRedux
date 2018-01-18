package com.zeyad.rxredux.core.redux;

import java.util.List;

/**
 * @author by ZIaDo on 3/27/17.
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
