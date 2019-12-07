package com.zeyad.rxredux.core.viewmodel

sealed class Result<S, I> {
    abstract val intent: I
}

sealed class EffectResult<E, I> : Result<E, I>()

internal data class LoadingEffectResult<I>(override val intent: I) : EffectResult<Nothing, I>()

internal data class ErrorEffectResult<I>(val error: Throwable,
                                         override val intent: I) : EffectResult<Nothing, I>()

data class SuccessEffectResult<E, I>(val bundle: E,
                                     override val intent: I) : EffectResult<E, I>()

internal data class SuccessResult<R, I>(val bundle: R,
                                        override val intent: I) : Result<R, I>()
