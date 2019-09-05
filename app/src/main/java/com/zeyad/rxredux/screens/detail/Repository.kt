package com.zeyad.rxredux.screens.detail

import android.os.Parcelable
import com.zeyad.rxredux.screens.User
import io.realm.RealmObject
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Repository(var id: Int = 0,
                      var name: String = "",
                      internal var owner: User? = null) : RealmObject(), Parcelable
