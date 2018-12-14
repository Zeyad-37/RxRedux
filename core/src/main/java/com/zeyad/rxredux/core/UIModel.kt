package com.zeyad.rxredux.core

sealed class UIModel<S> {
    abstract val event: BaseEvent<*>
    abstract val isLoading: Boolean

    override fun toString() = "stateEvent: $event"
}

data class LoadingState<S>(val bundle: S,
                           override val event: BaseEvent<*>,
                           override val isLoading: Boolean = true) : UIModel<S>() {
    override fun toString() = "State: Loading, " + super.toString()
}

data class ErrorState<S>(val bundle: S,
                         val error: Throwable,
                         override val event: BaseEvent<*>,
                         override val isLoading: Boolean = false) : UIModel<S>() {
    override fun toString() = "State: Error, Throwable: $error, " + super.toString()
}

data class SuccessState<S>(val bundle: S,
                           override val event: BaseEvent<*> = EmptyEvent,
                           override val isLoading: Boolean = false) : UIModel<S>() {
    override fun toString() = "State: Success, Bundle: $bundle, " + super.toString()
}
