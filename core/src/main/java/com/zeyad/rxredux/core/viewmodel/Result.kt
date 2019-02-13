package com.zeyad.rxredux.core.viewmodel

import com.zeyad.rxredux.core.BaseEvent

sealed class Result<S> {
    abstract val event: BaseEvent<*>
}

data class LoadingResult(override val event: BaseEvent<*>) : Result<Nothing>()

data class ErrorResult(val error: Throwable,
                       override val event: BaseEvent<*>) : Result<Nothing>()

data class SuccessResult<S>(val bundle: S,
                            override val event: BaseEvent<*>) : Result<S>()
