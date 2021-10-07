package com.zeyad.rxredux.simplevm

import com.zeyad.rxredux.core.vm.rxvm.Effect
import com.zeyad.rxredux.core.vm.rxvm.Input
import com.zeyad.rxredux.core.vm.rxvm.Result
import com.zeyad.rxredux.core.vm.rxvm.State
import kotlinx.android.parcel.Parcelize
import kotlin.random.Random

sealed class MyInput : Input()
data class ChangeBackgroundButtonClickInput(val r: Int = Random.nextInt(255), val g: Int = Random.nextInt(255), val b: Int = Random.nextInt(255)) : MyInput()
object ShowDialogButtonClickInput : MyInput()
object ErrorInput : MyInput()

sealed class MyResult : Result
object ChangeBackgroundResult : MyResult()

sealed class MyEffect : Effect

object ShowDialogEffect : MyEffect()

sealed class MyState : State

@Parcelize
data class ColorBackgroundState(val color: Int) : MyState()

@Parcelize
object InitialState : MyState()
sealed class ModernResult
