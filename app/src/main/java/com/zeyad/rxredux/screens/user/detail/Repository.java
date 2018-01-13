package com.zeyad.rxredux.screens.user.detail;

import com.google.gson.annotations.SerializedName;
import com.zeyad.rxredux.screens.user.User;

import org.parceler.Parcel;

import io.realm.RealmObject;

/**
 * @author zeyad on 1/25/17.
 */
@Parcel
public class Repository extends RealmObject {
    @SerializedName("id")
    int id;
    @SerializedName("name")
    String name;
    @SerializedName("full_name")
    String fullName;
    @SerializedName("owner")
    User owner;

    public Repository() {
    }

    public static boolean isEmpty(Repository repository) {
        return repository == null
                || repository.name == null && repository.fullName == null && repository.owner == null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Repository that = (Repository) o;

        if (id != that.id) return false;
        if (!name.equals(that.name)) return false;
        if (!fullName.equals(that.fullName)) return false;
        return owner.equals(that.owner);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + fullName.hashCode();
        result = 31 * result + owner.hashCode();
        return result;
    }
}
