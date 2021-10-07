package com.zeyad.rxredux.core.vm.rxvm

import android.os.Parcelable

interface State : Parcelable

interface Result

class EmptyResult : Result

data class Error(val message: String, val cause: Throwable, val input: Input? = null)

interface Effect

open class Input(val showProgress: Boolean = true)

data class Progress(val isLoading: Boolean, val input: Input)
