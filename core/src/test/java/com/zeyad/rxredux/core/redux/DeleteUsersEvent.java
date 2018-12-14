package com.zeyad.rxredux.core.redux;

import android.os.Parcel;

import com.zeyad.rxredux.core.BaseEvent;

import java.util.List;

public final class DeleteUsersEvent implements BaseEvent<List<String>> {

    private final List<String> selectedItemsIds;

    public DeleteUsersEvent(List<String> selectedItemsIds) {
        this.selectedItemsIds = selectedItemsIds;
    }

    @Override
    public List<String> getPayLoad() {
        return selectedItemsIds;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
