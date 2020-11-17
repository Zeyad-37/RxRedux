package com.zeyad.rxredux.core.v2

import android.os.Parcelable

interface State : Parcelable

interface Outcome

interface Result : Outcome

data class Error(val message: String, val cause: Throwable, val input: Input? = null)

interface Effect : Outcome

open class Input(val showProgress: Boolean = true)

data class Progress(val isLoading: Boolean, val input: Input)
