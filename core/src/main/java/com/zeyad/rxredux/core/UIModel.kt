package com.zeyad.rxredux.core

import android.os.Parcel

/**
 * @author Zeyad Gasser.
 */
sealed class UIModel<S>(val event: BaseEvent<*>, val isLoading: Boolean = false) {
    override fun toString() = "stateEvent: $event"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UIModel<*>) return false

        if (event != other.event) return false
        if (isLoading != other.isLoading) return false

        return true
    }

    override fun hashCode(): Int {
        var result = event.hashCode()
        result = 31 * result + isLoading.hashCode()
        return result
    }
}

class LoadingState<S>(val bundle: S, event: BaseEvent<*>) : UIModel<S>(event, true) {
    override fun toString() = "State: Loading, " + super.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LoadingState<*>) return false
        if (!super.equals(other)) return false

        if (bundle != other.bundle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (bundle?.hashCode() ?: 0)
        return result
    }
}

class ErrorState<S>(val bundle: S, val error: Throwable, event: BaseEvent<*>) : UIModel<S>(event) {
    override fun toString() = "State: Error, " + super.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ErrorState<*>) return false
        if (!super.equals(other)) return false

        if (bundle != other.bundle) return false
        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (bundle?.hashCode() ?: 0)
        result = 31 * result + error.hashCode()
        return result
    }
}

class SuccessState<S>(val bundle: S, event: BaseEvent<*> = EmptyEvent) : UIModel<S>(event) {
    override fun toString() = "State: Success, " + "Bundle: $bundle, " + super.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SuccessState<*>) return false
        if (!super.equals(other)) return false

        if (bundle != other.bundle) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + (bundle?.hashCode() ?: 0)
        return result
    }
}

val EmptyEvent = object : BaseEvent<Any> {
    override fun getPayLoad(): Unit = Unit

    override fun writeToParcel(dest: Parcel?, flags: Int) = Unit

    override fun describeContents() = 0
}
