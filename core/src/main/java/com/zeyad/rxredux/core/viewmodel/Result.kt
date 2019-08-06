package com.zeyad.rxredux.core.viewmodel

sealed class Result<S, I> {
    abstract val event: I
}

sealed class EffectResult<S, I> : Result<S, I>()

data class LoadingEffectResult<I>(override val event: I) : EffectResult<Nothing, I>()

data class ErrorEffectResult<I>(val error: Throwable,
                                               override val event: I) : EffectResult<Nothing, I>()

data class SuccessEffectResult<S, I>(val bundle: S,
                                     override val event: I) : EffectResult<S, I>()

data class SuccessResult<S, I>(val bundle: S,
                               override val event: I) : Result<S, I>()
