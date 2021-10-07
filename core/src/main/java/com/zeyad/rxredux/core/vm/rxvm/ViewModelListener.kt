package com.zeyad.rxredux.core.vm.rxvm

interface ViewModelListener {
    var effects: (effect: Effect) -> Unit
    var states: (state: State) -> Unit
    var errors: (error: Error) -> Unit
    var progress: (progress: Progress) -> Unit
}
