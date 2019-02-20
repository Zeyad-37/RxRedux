package com.zeyad.rxredux.core

sealed class PModel<S> {
    abstract val event: BaseEvent<*>
    abstract val bundle: S

    override fun toString() = "stateEvent: $event"
}

sealed class PEffect<S> : PModel<S>()
sealed class PState<S> : PModel<S>()

data class LoadingState<S>(override val bundle: S,
                           override val event: BaseEvent<*>) : PState<S>() {
    override fun toString() = "State: Loading, " + super.toString()
}

data class ErrorState<S>(val error: Throwable,
                         val errorMessage: String,
                         override val bundle: S,
                         override val event: BaseEvent<*>) : PState<S>() {
    override fun toString() = "State: Error, ${super.toString()}, Throwable: $error"
}

data class SuccessState<S>(override val bundle: S,
                           override val event: BaseEvent<*> = EmptyEvent) : PState<S>() {
    override fun toString() = "State: Success, ${super.toString()}, Bundle: $bundle"
}

data class SuccessEffect<S>(override val bundle: S,
                            override val event: BaseEvent<*> = EmptyEvent) : PEffect<S>() {
    override fun toString() = "State: Success, ${super.toString()}, Bundle: $bundle"
}
