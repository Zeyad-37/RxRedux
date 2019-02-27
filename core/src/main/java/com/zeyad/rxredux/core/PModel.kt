package com.zeyad.rxredux.core

sealed class PModel<S> {
    abstract val event: BaseEvent<*>
    abstract val bundle: S

    override fun toString() = "stateEvent: $event"
}

sealed class PEffect<S> : PModel<S>()

data class LoadingEffect<S>(override val bundle: S,
                            override val event: BaseEvent<*>) : PEffect<S>() {
    override fun toString() = "State: Loading, " + super.toString()
}

data class ErrorEffect<S>(val error: Throwable,
                          val errorMessage: Message,
                          override val bundle: S,
                          override val event: BaseEvent<*>) : PEffect<S>() {
    override fun toString() = "State: Error, ${super.toString()}, Throwable: $error"
}

data class SuccessEffect<S>(override val bundle: S,
                            override val event: BaseEvent<*> = EmptyEvent) : PEffect<S>() {
    override fun toString() = "State: Success, ${super.toString()}, Bundle: $bundle"
}

data class SuccessState<S>(override val bundle: S,
                           override val event: BaseEvent<*> = EmptyEvent) : PModel<S>() {
    override fun toString() = "State: Success, ${super.toString()}, Bundle: $bundle"
}
