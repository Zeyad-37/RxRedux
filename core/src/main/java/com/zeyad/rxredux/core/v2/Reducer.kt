package com.zeyad.rxredux.core.v2

interface Reducer<S : State, R : Result> {
    fun reduce(state: S, result: R): S
}
