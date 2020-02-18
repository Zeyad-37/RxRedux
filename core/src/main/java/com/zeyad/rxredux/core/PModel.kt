package com.zeyad.rxredux.core

sealed class PModel<S, I> {
    abstract val intent: I?

    override fun toString() = "Intent: $intent"
}

sealed class PState<S, I> : PModel<S, I>() {
    abstract val bundle: S
}

sealed class PEffect<E, I> : PModel<E, I>() {
    abstract val bundle: E?
}

internal data class LoadingEffect<E, I>(override val bundle: E,
                                        override val intent: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Loading, " + super.toString()
}

internal data class ErrorEffect<E, I>(val error: Throwable,
                                      val errorMessage: String,
                                      override val bundle: E?,
                                      override val intent: I? = null) : PEffect<E, I>() {
    override fun toString() = "Effect: Error, ${super.toString()}, Throwable: $error"
}

internal data class SuccessEffect<E, I>(override val bundle: E,
                                        override val intent: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Success, ${super.toString()}, Bundle: $bundle"
}

internal object InitialSuccessEffect : PEffect<Unit?, Any?>() {
    override val bundle: Unit = Unit
    override val intent: Any? = null
}

internal data class EmptySuccessEffect<E, I>(override val intent: I? = null,
                                             override val bundle: E? = null) : PEffect<E, I>()

data class SuccessState<S, I>(override val bundle: S,
                              override val intent: I?) : PState<S, I>() {
    override fun toString() = "State: Success, ${super.toString()}, Bundle: $bundle"
}

internal object EmptySuccessState : PState<Unit, Any?>() {
    override val intent: Any? = null
    override val bundle: Unit = Unit
}
