package com.zeyad.rxredux.core.viewmodel

sealed class Result<S, I> {
    abstract val intent: I
}

sealed class EffectResult<S, I> : Result<S, I>()

internal data class LoadingEffectResult<I>(override val intent: I) : EffectResult<Nothing, I>()

internal data class ErrorEffectResult<I>(val error: Throwable,
                                         override val intent: I) : EffectResult<Nothing, I>()

data class SuccessEffectResult<S, I>(val bundle: S,
                                     override val intent: I) : EffectResult<S, I>()

internal data class SuccessResult<S, I>(val bundle: S,
                                        override val intent: I) : Result<S, I>()
