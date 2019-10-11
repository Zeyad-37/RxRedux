package com.zeyad.rxredux.core.viewmodel

sealed class Result<R, I> {
    abstract val event: I
}

sealed class EffectResult<R, I> : Result<R, I>()

data class LoadingEffectResult<I>(override val event: I) : EffectResult<Nothing, I>()

data class ErrorEffectResult<I>(val error: Throwable,
                                override val event: I) : EffectResult<Nothing, I>()

data class SuccessEffectResult<R, I>(val bundle: R,
                                     override val event: I) : EffectResult<R, I>()

data class SuccessResult<R, I>(val bundle: R,
                               override val event: I) : Result<R, I>()
