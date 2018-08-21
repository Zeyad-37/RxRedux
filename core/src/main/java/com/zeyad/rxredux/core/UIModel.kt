package com.zeyad.rxredux.core

import android.os.Parcel

/**
 * @author Zeyad Gasser.
 */
sealed class UIModel<S>(val event: BaseEvent<*>, val isLoading: Boolean = false) {
    override fun toString() = "stateEvent: $event"
}

class LoadingState<S>(val bundle: S, event: BaseEvent<*>) : UIModel<S>(event, true) {
    override fun toString() = "State: Loading, " + super.toString()
}

class ErrorState<S>(val error: Throwable, event: BaseEvent<*>) : UIModel<S>(event) {
    override fun toString() = "State: Error, " + super.toString()
}

class SuccessState<S>(val bundle: S, event: BaseEvent<*> = EmptyEvent) : UIModel<S>(event) {
    override fun toString() = "State: Success, " + "Bundle: $bundle, " + super.toString()
}

class EmptyState<S> : UIModel<S>(EmptyEvent) {
    override fun toString() = "State: Empty, " + super.toString()
}

val EmptyEvent = object : BaseEvent<Any?> {
    override fun getPayLoad(): Unit = Unit

    override fun writeToParcel(dest: Parcel?, flags: Int) = Unit

    override fun describeContents() = 0
}
