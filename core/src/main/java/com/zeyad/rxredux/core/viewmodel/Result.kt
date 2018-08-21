package com.zeyad.rxredux.core.viewmodel

import com.zeyad.rxredux.core.BaseEvent

/**
 * @author Zeyad Gasser.
 */
sealed class Result<S>(val event: BaseEvent<*>, val isLoading: Boolean = false)

class LoadingResult(event: BaseEvent<*>) : Result<Nothing>(event, true)
class ErrorResult(val error: Throwable, event: BaseEvent<*>) : Result<Nothing>(event)
class SuccessResult<S>(val bundle: S, event: BaseEvent<*>) : Result<S>(event)
