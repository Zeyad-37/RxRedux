package com.zeyad.rxredux.core.v2

enum class InputStrategy(val interval: Long) {
    NONE(0L), THROTTLE(200L), DEBOUNCE(500L)
}
