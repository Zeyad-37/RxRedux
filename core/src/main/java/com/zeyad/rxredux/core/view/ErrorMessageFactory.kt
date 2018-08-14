package com.zeyad.rxredux.core.view

/**
 * @author Zeyad Gasser.
 */
interface ErrorMessageFactory {
    fun getErrorMessage(throwable: Throwable, event: String): String
}