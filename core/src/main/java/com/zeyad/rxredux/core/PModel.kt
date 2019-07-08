package com.zeyad.rxredux.core

sealed class PModel<S, I : BaseEvent<*>> {
    abstract val event: I?
    abstract val bundle: S

    override fun toString() = "Event: $event"
}

sealed class PEffect<E, I : BaseEvent<*>> : PModel<E, I>()

data class LoadingEffect<E, I : BaseEvent<*>>(override val bundle: E,
                                              override val event: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Loading, " + super.toString()
}

data class ErrorEffect<E, I : BaseEvent<*>>(val error: Throwable,
                                            val errorMessage: Message,
                                            override val bundle: E,
                                            override val event: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Error, ${super.toString()}, Throwable: $error"
}

data class SuccessEffect<E, I : BaseEvent<*>>(override val bundle: E,
                                              override val event: I) : PEffect<E, I>() {
    override fun toString() = "Effect: Success, ${super.toString()}, Bundle: $bundle"
}

data class EmptySuccessEffect(override val bundle: Unit = Unit,
                              override val event: EmptyEvent? = null) : PEffect<Unit, EmptyEvent>()

data class SuccessState<S, I : BaseEvent<*>>(override val bundle: S,
                                             override val event: I?) : PModel<S, I>() {
    override fun toString() = "State: Success, ${super.toString()}, Bundle: $bundle"
}

data class EmptySuccessState(override val bundle: Unit = Unit,
                             override val event: EmptyEvent? = null) : PModel<Unit, EmptyEvent>()
