package com.zeyad.rxredux.core.v2

interface LoggingListener<I : Input, R : Result, S : State, E : Effect> {
    var progress: (progress: Progress) -> Unit
    var effects: (effect: E) -> Unit
    var states: (state: S) -> Unit
    var errors: (error: Error) -> Unit
    var results: (result: R) -> Unit
    var inputs: (input: I) -> Unit
}

class LoggingListenerHelper<I : Input, R : Result, S : State, E : Effect> : LoggingListener<I, R, S, E> {
    override var progress: (progress: Progress) -> Unit = {}
    override var effects: (effect: E) -> Unit = {}
    override var states: (state: S) -> Unit = {}
    override var results: (result: R) -> Unit = {}
    override var inputs: (input: I) -> Unit = {}
    override var errors: (error: Error) -> Unit = {}

    fun errors(errors: (error: Error) -> Unit) {
        this.errors = errors
    }

    fun effects(effects: (effect: E) -> Unit) {
        this.effects = effects
    }

    fun states(states: (state: S) -> Unit) {
        this.states = states
    }

    fun results(results: (result: R) -> Unit) {
        this.results = results
    }

    fun inputs(inputs: (input: I) -> Unit) {
        this.inputs = inputs
    }

    fun progress(progress: (progress: Progress) -> Unit) {
        this.progress = progress
    }
}
