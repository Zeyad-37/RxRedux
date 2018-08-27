package com.zeyad.rxredux.core.viewmodel

import com.zeyad.rxredux.core.BaseEvent

/**
 * @author Zeyad Gasser.
 */
sealed class Result<S>(val event: BaseEvent<*>, val isLoading: Boolean = false) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Result<*>) return false

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

class LoadingResult(event: BaseEvent<*>) : Result<Nothing>(event, true) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LoadingResult) return false
        if (!super.equals(other)) return false
        return true
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }
}

class ErrorResult(val error: Throwable, event: BaseEvent<*>) : Result<Nothing>(event) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ErrorResult) return false
        if (!super.equals(other)) return false

        if (error != other.error) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + error.hashCode()
        return result
    }
}

class SuccessResult<S>(val bundle: S, event: BaseEvent<*>) : Result<S>(event) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SuccessResult<*>) return false
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
