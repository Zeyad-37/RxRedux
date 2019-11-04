package com.zeyad.rxredux.core

sealed class PModel<S, I> {
    abstract val intent: I?
    abstract val bundle: S

    override fun toString() = "Intent: $intent"
}

sealed class PEffect<E, I> : PModel<E, I>()

internal data class LoadingEffect<E, I>(override val bundle: E,
                                        override val intent: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Loading, " + super.toString()
}

internal data class ErrorEffect<E, I>(val error: Throwable,
                                      val errorMessage: String,
                                      override val bundle: E,
                                      override val intent: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Error, ${super.toString()}, Throwable: $error"
}

internal data class SuccessEffect<E, I>(override val bundle: E,
                                        override val intent: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Success, ${super.toString()}, Bundle: $bundle"
}

data class EmptySuccessEffect(override val bundle: Unit = Unit,
                              override val intent: Any? = null) : PEffect<Unit, Any?>()

data class SuccessState<S, I>(override val bundle: S,
                              override val intent: I?) : PModel<S, I>() {
    override fun toString() = "State: Success, ${super.toString()}, Bundle: $bundle"
}

internal data class EmptySuccessState(override val bundle: Unit = Unit,
                                      override val intent: Any? = null) : PModel<Unit, Any?>()
