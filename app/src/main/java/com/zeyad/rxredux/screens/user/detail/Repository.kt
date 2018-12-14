package com.zeyad.rxredux.screens.user.detail

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Repository(@SerializedName("id")
                      var id: Int = 0,
                      @SerializedName("name")
                      var name: String = "") : RealmObject(), Parcelable
