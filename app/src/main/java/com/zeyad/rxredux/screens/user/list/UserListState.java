package com.zeyad.rxredux.screens.user.list;

import java.util.ArrayList;
import java.util.List;

import org.parceler.Parcel;
import org.parceler.Transient;

/**
 * @author by ZIaDo on 1/28/17.
 */
@Parcel
public class UserListState {
    @Transient
    List<User> users;
    @Transient
    List<User> searchList;
    long lastId;

    UserListState() {
        users = new ArrayList<>();
    }

    private UserListState(Builder builder) {
        users = builder.users;
        searchList = builder.searchList;
        lastId = builder.lastId;
    }

    static Builder builder() {
        return new Builder();
    }

    List<User> getUsers() {
        return users;
    }

    List<User> getSearchList() {
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

    static class Builder {
        List<User> users;
        List<User> searchList;
        long lastId;

        Builder() {
        }

        Builder users(List<User> value) {
            users = value;
            return this;
        }

        Builder searchList(List<User> value) {
            searchList = value;
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
