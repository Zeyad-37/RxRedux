package com.zeyad.rxredux

import com.zeyad.rxredux.core.vm.rxvm.Effect
import com.zeyad.rxredux.core.vm.rxvm.Input
import com.zeyad.rxredux.core.vm.rxvm.Result
import com.zeyad.rxredux.core.vm.rxvm.State
import kotlinx.android.parcel.Parcelize

sealed class MyInput : Input()
object ChangeBackgroundButtonClickInput : MyInput()
object ShowDialogButtonClickInput : MyInput()
object ErrorInput : MyInput()

sealed class MyResult : Result
object ChangeBackgroundResult : MyResult()

sealed class MyEffect : Effect

object ShowDialogEffect : MyEffect()

sealed class MyState : State

@Parcelize
object RedBackgroundState : MyState()

@Parcelize
object InitialState : MyState()
sealed class ModernResult
