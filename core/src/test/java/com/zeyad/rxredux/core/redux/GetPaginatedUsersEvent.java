package com.zeyad.rxredux.core.redux;

import android.os.Parcel;

import com.zeyad.rxredux.core.BaseEvent;

public class GetPaginatedUsersEvent implements BaseEvent<Long> {

    private final long lastId;

    public GetPaginatedUsersEvent(long lastId) {
        this.lastId = lastId;
    }

    @Override
    public Long getPayLoad() {
        return lastId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
