package com.zeyad.rxredux.core.vm

import com.zeyad.rxredux.core.vm.rxvm.Result
import com.zeyad.rxredux.core.vm.rxvm.State

interface Reducer<S : State, R : Result> {
    fun reduce(state: S, result: R): S
}
