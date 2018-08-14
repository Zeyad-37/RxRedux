package com.zeyad.rxredux.core

/**
 * @author Zeyad Gasser.
 */
sealed class UIModel<S>(val event: String, val isLoading: Boolean = false) {
    override fun toString() = "event: $event"
}

class LoadingState<S>(val bundle: S, event: String) : UIModel<S>(event, true) {
    override fun toString() = "State: Loading, " + super.toString()
}

class ErrorState<S>(val error: Throwable, event: String) : UIModel<S>(event) {
    override fun toString() = "State: Error, " + super.toString()
}

class SuccessState<S>(val bundle: S, event: String = "") : UIModel<S>(event) {
    override fun toString() = "State: Success, " + "Bundle: $bundle, " + super.toString()
}

class EmptyState<S> : UIModel<S>("") {
    override fun toString() = "State: Empty, " + super.toString()
}
