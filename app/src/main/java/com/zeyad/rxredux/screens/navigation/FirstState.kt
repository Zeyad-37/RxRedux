package com.zeyad.rxredux.screens.navigation

import android.content.Intent
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

sealed class FirstState : Parcelable

@Parcelize
object EmptyFirstState : FirstState()

@Parcelize
object FullFirstState : FirstState()

sealed class FirstEffect : Parcelable

@Parcelize
data class NavigateToEffect(val intent: Intent) : FirstEffect()