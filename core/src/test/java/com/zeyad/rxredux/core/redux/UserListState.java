package com.zeyad.rxredux.core.redux;

import android.os.Parcel;
import android.os.Parcelable;

import com.zeyad.gadapter.ItemInfo;

import java.util.ArrayList;
import java.util.List;

public class UserListState implements Parcelable {
    public static final Parcelable.Creator<UserListState> CREATOR = new Parcelable.Creator<UserListState>() {
        @Override
        public UserListState createFromParcel(Parcel source) {
            return new UserListState(source);
        }

        @Override
        public UserListState[] newArray(int size) {
            return new UserListState[size];
        }
    };
    //    @Transient
    List<ItemInfo> users;
    //    @Transient
    List<ItemInfo> searchList;
    long lastId;

    UserListState() {
        users = new ArrayList<>();
    }

    private UserListState(Builder builder) {
        users = builder.users;
        searchList = builder.searchList;
        lastId = builder.lastId;
    }

    protected UserListState(Parcel in) {
        this.users = new ArrayList<>();
        //        in.readList(this.users, User.class.getClassLoader());
        this.searchList = new ArrayList<>();
        //        in.readList(this.searchList, User.class.getClassLoader());
        this.lastId = in.readLong();
    }

    static Builder builder() {
        return new Builder();
    }

    List<ItemInfo> getUsers() {
        return users;
    }

    List<ItemInfo> getSearchList() {
        return searchList;
    }

    long getLastId() {
        return lastId;
    }

    @Override
    public int hashCode() {
        return (int) (lastId ^ (lastId >>> 32));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof UserListState))
            return false;
        UserListState that = (UserListState) o;
        return lastId == that.lastId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //        dest.writeList(this.users);
        //        dest.writeList(this.searchList);
        dest.writeLong(this.lastId);
    }

    static class Builder {
        List<ItemInfo> users;
        List<ItemInfo> searchList;
        long lastId;

        Builder() {
        }

        Builder users(List<User> value) {
//            users = Observable.fromIterable(value)
//                    .map(user -> new ItemInfo(user, 1).setId(user.getId()))
//                    .toList(value.size()).blockingGet();
            return this;
        }

        Builder searchList(List<User> value) {
//            searchList = Observable.fromIterable(value)
//                    .map(user -> new ItemInfo(user, 1).setId(user.getId()))
//                    .toList().blockingGet();
            return this;
        }

        Builder lastId(long value) {
            lastId = value;
            return this;
        }

        UserListState build() {
            return new UserListState(this);
        }
    }
}