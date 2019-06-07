package com.zeyad.rxredux.core.viewmodel

import com.zeyad.rxredux.core.BaseEvent

sealed class Result<S, I> {
    abstract val event: BaseEvent<I>
}

sealed class EffectResult<S, I> : Result<S, I>()

data class LoadingEffectResult<I>(override val event: BaseEvent<I>) : EffectResult<Nothing, I>()

data class ErrorEffectResult<I>(val error: Throwable,
                                override val event: BaseEvent<I>) : EffectResult<Nothing, I>()

data class SuccessEffectResult<S, I>(val bundle: S,
                                     override val event: BaseEvent<I>) : EffectResult<S, I>()

data class SuccessResult<S, I>(val bundle: S,
                               override val event: BaseEvent<I>) : Result<S, I>()
