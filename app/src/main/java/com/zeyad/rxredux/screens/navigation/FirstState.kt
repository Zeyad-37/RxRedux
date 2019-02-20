package com.zeyad.rxredux.screens.navigation

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class FirstState : Parcelable

@Parcelize
object EmptyFirstState : FirstState()

@Parcelize
object FullFirstState : FirstState()
