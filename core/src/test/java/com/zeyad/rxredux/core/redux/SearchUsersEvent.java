package com.zeyad.rxredux.core.redux;

import android.os.Parcel;

import com.zeyad.rxredux.core.BaseEvent;

public class SearchUsersEvent implements BaseEvent {

    private final String query;

    public SearchUsersEvent(String s) {
        query = s;
    }

    @Override
    public String getPayLoad() {
        return query;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
