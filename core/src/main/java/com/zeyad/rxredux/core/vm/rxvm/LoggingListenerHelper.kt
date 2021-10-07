package com.zeyad.rxredux.core.vm.rxvm

class LoggingListenerHelper : LoggingListener {
    override var progress: (progress: Progress) -> Unit = {}
    override var effects: (effect: Effect) -> Unit = {}
    override var states: (state: State) -> Unit = {}
    override var results: (result: Result) -> Unit = {}
    override var inputs: (inputs: Input) -> Unit = {}
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

    fun results(results: (result: Result) -> Unit) {
        this.results = results
    }

    fun inputs(inputs: (inputs: Input) -> Unit) {
        this.inputs = inputs
    }

    fun progress(progress: (progress: Progress) -> Unit) {
        this.progress = progress
    }
}
