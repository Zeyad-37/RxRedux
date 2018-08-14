package com.zeyad.rxredux.core.viewmodel

/**
 * @author Zeyad Gasser.
 */
sealed class Result<S>(val event: String, val isLoading: Boolean = false)

class LoadingResult(event: String) : Result<Nothing>(event, true)
class ErrorResult(val error: Throwable, event: String) : Result<Nothing>(event)
class SuccessResult<S>(val bundle: S, event: String) : Result<S>(event)
