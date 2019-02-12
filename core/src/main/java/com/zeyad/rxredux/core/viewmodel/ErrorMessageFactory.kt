package com.zeyad.rxredux.core.viewmodel

import com.zeyad.rxredux.core.BaseEvent

typealias ErrorMessageFactory = (throwable: Throwable, event: BaseEvent<*>) -> String