package com.zeyad.rxredux.core

/**
 * @author ZIaDo on 2/27/18.
 */
interface ErrorMessageFactory {
    fun getErrorMessage(throwable: Throwable): String
}