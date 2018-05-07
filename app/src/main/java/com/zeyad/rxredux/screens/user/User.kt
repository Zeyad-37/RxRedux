package com.zeyad.rxredux.screens.user

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * @author zeyad on 1/10/17.
 */
@Parcelize
data class User(@PrimaryKey
                @SerializedName(LOGIN)
                var login: String? = null,
                @SerializedName(ID)
                var id: Int = 0,
                @SerializedName(AVATAR_URL)
                var avatarUrl: String? = null) : RealmObject(), Parcelable {

    companion object {
        const val LOGIN = "login"
        private const val ID = "id"
        private const val AVATAR_URL = "avatar_url"
    }
}
