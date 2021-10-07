package com.zeyad.rxredux.core.vm.rxvm

class ViewModelListenerHelper : ViewModelListener {
    override var effects: (effect: Effect) -> Unit = {}
    override var states: (state: State) -> Unit = {}
    override var progress: (progress: Progress) -> Unit = {}
    override var errors: (error: Error) -> Unit = {}

    fun errors(errors: (error: Error) -> Unit) {
        this.errors = errors
    }

    fun effects(effects: (effect: Effect) -> Unit) {
        this.effects = effects
    }

    fun states(states: (state: State) -> Unit) {
        this.states = states
    }

    fun progress(progress: (progress: Progress) -> Unit) {
        this.progress = progress
    }
}
