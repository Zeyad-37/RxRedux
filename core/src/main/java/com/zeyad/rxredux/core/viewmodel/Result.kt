package com.zeyad.rxredux.core.viewmodel

import com.zeyad.rxredux.core.BaseEvent

sealed class Result<S> {
    abstract val event: BaseEvent<*>
}

sealed class EffectResult<S> : Result<S>()

data class LoadingEffectResult(override val event: BaseEvent<*>) : EffectResult<Nothing>()

data class ErrorEffectResult(val error: Throwable,
                             override val event: BaseEvent<*>) : EffectResult<Nothing>()

data class SuccessEffectResult<S>(val bundle: S,
                                  override val event: BaseEvent<*>) : EffectResult<S>()

data class SuccessResult<S>(val bundle: S,
                            override val event: BaseEvent<*>) : Result<S>()
