package com.zeyad.rxredux.core.vm.viewmodel

sealed class Result<S, I> {
    abstract val input: I
}

sealed class EffectResult<E, I> : Result<E, I>()

internal data class LoadingEffectResult<I>(override val input: I) : EffectResult<Nothing, I>()

internal data class ErrorEffectResult<I>(val error: Throwable,
                                         override val input: I) : EffectResult<Nothing, I>()

object EmptyEffectResult : EffectResult<Unit, Any?>() {
    override val input: Any? = null
}

data class SuccessEffectResult<E, I>(val bundle: E,
                                     override val input: I) : EffectResult<E, I>()

internal data class SuccessResult<R, I>(val bundle: R,
                                        override val input: I) : Result<R, I>()
