package com.zeyad.rxredux.core.view

import com.zeyad.rxredux.core.BaseEvent

/**
 * @author Zeyad Gasser.
 */
interface ErrorMessageFactory {
    fun getErrorMessage(throwable: Throwable, event: BaseEvent<*>): String
}