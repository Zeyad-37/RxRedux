package com.zeyad.rxredux.core.verification

import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import kotlin.reflect.KClass

class GraphVerifier {

    fun <I : BaseEvent<*>, R, S : Parcelable, E : Any> verify(vm: IBaseViewModel<I, R, S, E>,
                                                              events: List<I>,
                                                              states: List<S>,
                                                              effects: List<E>,
                                                              results: List<R>): Boolean {
        return Graph().run {
            fill(vm, events, states, effects, results)
            validate(states, effects)
        }
    }

    private fun <I : BaseEvent<*>, R, S : Parcelable, E : Any> Graph.fill(vm: IBaseViewModel<I, R, S, E>,
                                                                          events: List<I>,
                                                                          states: List<S>,
                                                                          effects: List<E>,
                                                                          results: List<R>) {
        fillByReducingEventsWithStates(vm, events, states)
        fillByReducingEventsWithEffects(vm, events, effects)
        fillByReducingStatesWithResults(vm, states, results)
    }

    private fun <I : BaseEvent<*>, R, S : Parcelable, E : Any> Graph.fillByReducingStatesWithResults(vm: IBaseViewModel<I, R, S, E>,
                                                                                                     states: List<S>,
                                                                                                     results: List<R>) {
        val graphStates = mutableSetOf<KClass<*>>()
        for (state in states) {
            for (result in results) {
                try {
                    graphStates.add(vm.stateReducer(result, state)::class)
                } catch (exception: Exception) {
                    continue
                }
            }
            insertWisely(state::class, graphStates)
        }
    }

    private fun <I : BaseEvent<*>, R, S : Parcelable, E : Any> Graph.fillByReducingEventsWithEffects(vm: IBaseViewModel<I, R, S, E>,
                                                                                                     events: List<I>,
                                                                                                     effects: List<E>) {
        val graphEffects: MutableSet<KClass<*>> = mutableSetOf()
        for (effect in effects) {
            for (event in events) {
                reduceEvents(vm, event, effect, graphEffects)
            }
            insertWisely(effect::class, graphEffects)
        }
    }

    private fun <I : BaseEvent<*>, R, S : Parcelable, E : Any> Graph.fillByReducingEventsWithStates(vm: IBaseViewModel<I, R, S, E>,
                                                                                                    events: List<I>,
                                                                                                    states: List<S>) {
        val graphEffects: MutableSet<KClass<*>> = mutableSetOf()
        for (state in states) {
            for (event in events) {
                reduceEvents(vm, event, state, graphEffects)
            }
            insertWisely(state::class, graphEffects)
        }
    }

    private fun <I : BaseEvent<*>, R, S : Parcelable, E : Any> reduceEvents(vm: IBaseViewModel<I, R, S, E>,
                                                                            event: I,
                                                                            effect: Any,
                                                                            graphEffects: MutableSet<KClass<*>>) {
        val result = try {
            vm.reduceEventsToResults(event, effect).blockingFirst()
        } catch (exception: Exception) {
            return
        }
        if (result is SuccessEffectResult<*, *>) {
            graphEffects.add(result.bundle!!::class)
        }
    }

    private fun Graph.insertWisely(vertex: KClass<*>, neighbours: MutableSet<KClass<*>>) {
        if (adjVertices.containsKey(vertex)) {
            adjVertices[vertex] = neighbours.plus(getAdjVerticesFor(vertex).asIterable())
        } else {
            adjVertices[vertex] = neighbours.toSet()
        }
        neighbours.clear()
    }

    private fun <S : Parcelable, E : Any> Graph.validate(states: List<S>, effects: List<E>): Boolean {
        val roots =
                states.filter { it.javaClass.interfaces.contains(Root::class.java) }.map { it::class }
        val leaves =
                states.filter { it.javaClass.interfaces.contains(Leaf::class.java) }.map { it::class }
                        .plus(effects.filter { it.javaClass.interfaces.contains(Leaf::class.java) }.map { it::class })
        return roots.isNotEmpty() && leaves.isNotEmpty() && roots.all { validateCore(it, leaves) }
    }

    private fun Graph.validateCore(root: KClass<*>, leaves: List<KClass<*>>): Boolean {
        val calculatedLeaves =
                depthFirstTraversal(root).filter { it.java.interfaces.contains(Leaf::class.java) }.toList()
        return calculatedLeaves.containsAll(leaves) && calculatedLeaves.size == leaves.size
    }
}
