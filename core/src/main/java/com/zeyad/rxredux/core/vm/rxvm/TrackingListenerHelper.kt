package com.zeyad.rxredux.core.vm.rxvm

class TrackingListenerHelper : TrackingListener {
    override var progress: (progress: Progress) -> Unit = {}
    override var effects: (effect: Effect, input: Input) -> Unit = { _, _ -> /* empty */ }
    override var states: (state: State, input: Input) -> Unit = { _, _ -> /* empty */ }
    override var results: (result: Result, input: Input) -> Unit = { _, _ -> /* empty */ }
    override var inputs: (inputs: Input) -> Unit = {}
    override var errors: (error: Error) -> Unit = {}

    fun errors(errors: (error: Error) -> Unit) {
        this.errors = errors
    }

    fun effects(effects: (effect: Effect, input: Input) -> Unit) {
        this.effects = effects
    }

    fun states(states: (state: State, input: Input) -> Unit) {
        this.states = states
    }

    fun results(results: (result: Result, input: Input) -> Unit) {
        this.results = results
    }

    fun inputs(inputs: (inputs: Input) -> Unit) {
        this.inputs = inputs
    }

    fun progress(progress: (progress: Progress) -> Unit) {
        this.progress = progress
    }
}
