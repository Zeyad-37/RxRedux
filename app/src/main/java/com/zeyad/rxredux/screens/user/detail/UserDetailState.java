package com.zeyad.rxredux.screens.user.detail;

import java.util.List;

import org.parceler.Transient;

import com.zeyad.rxredux.screens.user.list.User;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author zeyad on 1/25/17.
 */
//@Parcel
public class UserDetailState implements Parcelable {
    public static final Parcelable.Creator<UserDetailState> CREATOR = new Parcelable.Creator<UserDetailState>() {
        @Override
        public UserDetailState createFromParcel(Parcel source) {
            return new UserDetailState(source);
        }

        @Override
        public UserDetailState[] newArray(int size) {
            return new UserDetailState[size];
        }
    };
    boolean isTwoPane;
    User user;
    @Transient
    List<Repository> repos;

    UserDetailState() {
        user = null;
        repos = null;
        isTwoPane = false;
    }

    private UserDetailState(Builder builder) {
        isTwoPane = builder.isTwoPane;
        user = builder.user;
        repos = builder.repos;
    }

    protected UserDetailState(Parcel in) {
        this.isTwoPane = in.readByte() != 0;
        this.user = in.readParcelable(User.class.getClassLoader());
    }

    public static Builder builder() {
        return new Builder();
    }

    boolean isTwoPane() {
        return isTwoPane;
    }

    User getUser() {
        return user;
    }

    List<Repository> getRepos() {
        return repos;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isTwoPane ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.user, flags);
    }

    public static class Builder {
        List<Repository> repos;
        User user;
        boolean isTwoPane;

        Builder() {
        }

        public Builder setRepos(List<Repository> value) {
            repos = value;
            return this;
        }

        public Builder setIsTwoPane(boolean value) {
            isTwoPane = value;
            return this;
        }

        public Builder setUser(User value) {
            user = value;
            return this;
        }

        public UserDetailState build() {
            return new UserDetailState(this);
        }
    }
}
