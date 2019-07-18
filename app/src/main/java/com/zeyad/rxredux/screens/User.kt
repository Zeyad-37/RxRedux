package com.zeyad.rxredux.screens

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
open class User(@PrimaryKey
                var login: String = "",
                var id: Long = 0,
                var avatarUrl: String = "") : RealmObject(), Parcelable {

    companion object {
        const val LOGIN = "login"
        private const val ID = "id"
        private const val AVATAR_URL = "avatar_url"
    }
}
