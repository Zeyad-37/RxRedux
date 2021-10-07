package com.zeyad.rxredux

import com.zeyad.rxredux.core.vm.Reducer

class MyReducer : Reducer<MyState, MyResult> {

    override fun reduce(state: MyState, result: MyResult): MyState {
        return when (result) {
            is ChangeBackgroundResult -> RedBackgroundState
        }
    }
}
