package com.zeyad.rxredux.core

import android.os.Parcel
import android.os.Parcelable

interface BaseEvent<T> : Parcelable {
    fun getPayLoad(): T
}

val EmptyEvent = object : BaseEvent<Any> {
    override fun getPayLoad(): Unit = Unit

    override fun writeToParcel(dest: Parcel?, flags: Int) = Unit

    override fun describeContents() = 0
}
