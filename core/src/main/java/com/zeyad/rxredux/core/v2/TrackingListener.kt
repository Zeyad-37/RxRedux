package com.zeyad.rxredux.core.v2

interface TrackingListener<I : Input, R : Result, S : State, E : Effect> {
    var progress: (progress: Progress) -> Unit
    var effects: (effect: E, input: I) -> Unit
    var states: (state: S, input: I) -> Unit
    var errors: (error: Error) -> Unit
    var results: (result: R, input: I) -> Unit
    var inputs: (input: I) -> Unit
}

class TrackingListenerHelper<I : Input, R : Result, S : State, E : Effect> : TrackingListener<I, R, S, E> {
    override var progress: (progress: Progress) -> Unit = {}
    override var effects: (effect: E, input: I) -> Unit = { _, _ -> /* empty */ }
    override var states: (state: S, input: I) -> Unit = { _, _ -> /* empty */ }
    override var results: (result: R, input: I) -> Unit = { _, _ -> /* empty */ }
    override var inputs: (input: I) -> Unit = {}
    override var errors: (error: Error) -> Unit = {}

    fun errors(errors: (error: Error) -> Unit) {
        this.errors = errors
    }

    fun effects(effects: (effect: E, input: I) -> Unit) {
        this.effects = effects
    }

    fun states(states: (state: S, input: I) -> Unit) {
        this.states = states
    }

    fun results(results: (result: R, input: I) -> Unit) {
        this.results = results
    }

    fun inputs(inputs: (input: I) -> Unit) {
        this.inputs = inputs
    }

    fun progress(progress: (progress: Progress) -> Unit) {
        this.progress = progress
    }
}
