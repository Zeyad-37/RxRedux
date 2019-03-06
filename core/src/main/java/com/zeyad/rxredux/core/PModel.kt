package com.zeyad.rxredux.core

sealed class PModel<S> {
    abstract val event: BaseEvent<*>
    abstract val bundle: S

    override fun toString() = "Event: $event"
}

sealed class PEffect<E> : PModel<E>()

data class LoadingEffect<E>(override val bundle: E,
                            override val event: BaseEvent<*>) : PEffect<E>() {
    override fun toString() = "Effect: Loading, " + super.toString()
}

data class ErrorEffect<E>(val error: Throwable,
                          val errorMessage: Message,
                          override val bundle: E,
                          override val event: BaseEvent<*>) : PEffect<E>() {
    override fun toString() = "Effect: Error, ${super.toString()}, Throwable: $error"
}

data class SuccessEffect<E>(override val bundle: E,
                            override val event: BaseEvent<*> = EmptyEvent) : PEffect<E>() {
    override fun toString() = "Effect: Success, ${super.toString()}, Bundle: $bundle"
}

data class SuccessState<S>(override val bundle: S,
                           override val event: BaseEvent<*> = EmptyEvent) : PModel<S>() {
    override fun toString() = "State: Success, ${super.toString()}, Bundle: $bundle"
}
