package com.zeyad.rxredux.screens

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(@PrimaryKey
                @SerializedName(LOGIN)
                var login: String = "",
                @SerializedName(ID)
                var id: Long = 0,
                @SerializedName(AVATAR_URL)
                var avatarUrl: String = "") : /*RealmObject(), */Parcelable {

    companion object {
        const val LOGIN = "login"
        private const val ID = "id"
        private const val AVATAR_URL = "avatar_url"
    }
}
