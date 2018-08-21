package com.zeyad.rxredux.core

import android.os.Parcelable

/**
 * @author Zeyad Gasser.
 */
interface BaseEvent<T> : Parcelable {
    fun getPayLoad(): T
}