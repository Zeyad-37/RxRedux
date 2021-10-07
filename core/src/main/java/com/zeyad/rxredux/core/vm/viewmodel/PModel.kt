package com.zeyad.rxredux.core.vm.viewmodel

import com.zeyad.rxredux.core.vm.rxvm.Input
import com.zeyad.rxredux.core.vm.rxvm.State

sealed class PModel<S, I> {
    abstract val input: I?

    override fun toString() = "Input: $input"
}

sealed class PState<S, I> : PModel<S, I>() {
    abstract val bundle: S
}

sealed class PEffect<E, I> : PModel<E, I>() {
    abstract val bundle: E?
}

internal data class LoadingEffect<E, I>(override val bundle: E,
                                        override val input: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Loading, " + super.toString()
}

internal data class ErrorEffect<E, I>(val error: Throwable,
                                      val errorMessage: String,
                                      override val bundle: E?,
                                      override val input: I? = null) : PEffect<E, I>() {
    override fun toString() = "Effect: Error, ${super.toString()}, Throwable: $error"
}

internal data class SuccessEffect<E, I>(override val bundle: E,
                                        override val input: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Success, ${super.toString()}, Bundle: $bundle"
}

internal object InitialSuccessEffect : PEffect<Unit?, Any?>() {
    override val bundle: Unit = Unit
    override val input: Any? = null
}

internal data class EmptySuccessEffect<E, I>(override val input: I? = null,
                                             override val bundle: E? = null) : PEffect<E, I>()

data class SuccessState<S, I>(override val bundle: S,
                              override val input: I?) : PState<S, I>() {
    override fun toString() = "State: Success, ${super.toString()}, Bundle: $bundle"
}

internal object EmptySuccessState : PState<State?, Input?>() {
    override val input: Input? = null
    override val bundle: State? = null
}
