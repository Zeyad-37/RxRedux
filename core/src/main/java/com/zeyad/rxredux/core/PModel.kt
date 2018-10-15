package com.zeyad.rxredux.core

sealed class PModel<S> {
    abstract val event: BaseEvent<*>
    abstract val isLoading: Boolean

    override fun toString() = "stateEvent: $event"
}

data class LoadingState<S>(val bundle: S,
                           override val event: BaseEvent<*>,
                           override val isLoading: Boolean = true) : PModel<S>() {
    override fun toString() = "State: Loading, " + super.toString()
}

data class ErrorState<S>(val bundle: S,
                         val error: Throwable,
                         override val event: BaseEvent<*>,
                         override val isLoading: Boolean = false) : PModel<S>() {
    override fun toString() = "State: Error, ${super.toString()}, Throwable: $error"
}

data class SuccessState<S>(val bundle: S,
                           override val event: BaseEvent<*> = EmptyEvent,
                           override val isLoading: Boolean = false) : PModel<S>() {
    override fun toString() = "State: Success, ${super.toString()},  Bundle: $bundle"
}
