package com.zeyad.rxredux.core.viewmodel

import com.zeyad.rxredux.core.BaseEvent

sealed class Result<S, I : BaseEvent<*>> {
    abstract val event: I
}

sealed class EffectResult<S, I : BaseEvent<*>> : Result<S, I>()

data class LoadingEffectResult<I : BaseEvent<*>>(override val event: I) : EffectResult<Nothing, I>()

data class ErrorEffectResult<I : BaseEvent<*>>(val error: Throwable,
                                               override val event: I) : EffectResult<Nothing, I>()

data class SuccessEffectResult<S, I : BaseEvent<*>>(val bundle: S,
                                                    override val event: I) : EffectResult<S, I>()

data class SuccessResult<S, I : BaseEvent<*>>(val bundle: S,
                                              override val event: I) : Result<S, I>()
