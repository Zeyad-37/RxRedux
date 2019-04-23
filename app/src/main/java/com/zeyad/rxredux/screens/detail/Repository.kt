package com.zeyad.rxredux.screens.detail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.zeyad.rxredux.screens.User
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Repository(@SerializedName("id")
                      var id: Int = 0,
                      @SerializedName("name")
                      var name: String = "",
                      @SerializedName("owner")
                      internal var owner: User? = null) : /*RealmObject(), */Parcelable
