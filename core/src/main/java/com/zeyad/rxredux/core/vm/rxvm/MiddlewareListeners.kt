package com.zeyad.rxredux.core.vm.rxvm

interface TrackingListener {
    var progress: (progress: Progress) -> Unit
    var effects: (effect: Effect, input: Input) -> Unit
    var states: (state: State, input: Input) -> Unit
    var errors: (error: Error) -> Unit
    var results: (result: Result, input: Input) -> Unit
    var inputs: (inputs: Input) -> Unit
}

interface LoggingListener {
    var progress: (progress: Progress) -> Unit
    var effects: (effect: Effect) -> Unit
    var states: (state: State) -> Unit
    var errors: (error: Error) -> Unit
    var results: (result: Result) -> Unit
    var inputs: (inputs: Input) -> Unit
}
