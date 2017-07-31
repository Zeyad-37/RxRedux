package com.zeyad.rxredux.screens.user.list;

import org.parceler.Parcel;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author zeyad on 1/10/17.
 */
@Parcel
public class User extends RealmObject {

    static final String LOGIN = "login";
    private static final String ID = "id", AVATAR_URL = "avatar_url";

    @PrimaryKey
    @SerializedName(LOGIN)
    String login;

    @SerializedName(ID)
    int id;

    @SerializedName(AVATAR_URL)
    String avatarUrl;

    public User() {
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + id;
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return id == user.id && (login != null ? login.equals(user.login)
                : user.login == null
                        && (avatarUrl != null ? avatarUrl.equals(user.avatarUrl) : user.avatarUrl == null));
    }
}
