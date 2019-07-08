package com.zeyad.rxredux.core.verification

import android.os.Parcelable
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import kotlin.reflect.KClass

class GraphVerifier {

    fun <R, S : Parcelable, E : Any> verify(vm: IBaseViewModel<R, S, E>,
                                            events: List<BaseEvent<*>>,
                                            states: List<S>,
                                            effects: List<E>,
                                            results: List<R>): Boolean {
        return Graph().run {
            fill(vm, events, states, effects, results)
            validate(states, effects)
        }
    }

    private fun <R, S : Parcelable, E : Any> Graph.fill(vm: IBaseViewModel<R, S, E>,
                                                        events: List<BaseEvent<*>>,
                                                        states: List<S>,
                                                        effects: List<E>,
                                                        results: List<R>) {
        fillByReducingEventsWithStates(vm, events, states)
        fillByReducingEventsWithEffects(vm, events, effects)
        fillByReducingStatesWithResults(vm, states, results)
    }

    private fun <R, S : Parcelable, E : Any> Graph.fillByReducingStatesWithResults(vm: IBaseViewModel<R, S, E>,
                                                                                   states: List<S>,
                                                                                   results: List<R>) {
        val graphStates = mutableListOf<KClass<*>>()
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

    private fun <R, S : Parcelable, E : Any> Graph.fillByReducingEventsWithEffects(vm: IBaseViewModel<R, S, E>,
                                                                                   events: List<BaseEvent<*>>,
                                                                                   effects: List<E>) {
        val graphEffects: MutableList<KClass<*>> = mutableListOf()
        for (effect in effects) {
            for (event in events) {
                reduceEvents(vm, event, effect, graphEffects)
            }
            insertWisely(effect::class, graphEffects)
        }
    }

    private fun <R, S : Parcelable, E : Any> Graph.fillByReducingEventsWithStates(vm: IBaseViewModel<R, S, E>,
                                                                                  events: List<BaseEvent<*>>,
                                                                                  states: List<S>) {
        val graphEffects: MutableList<KClass<*>> = mutableListOf()
        for (state in states) {
            for (event in events) {
                reduceEvents(vm, event, state, graphEffects)
            }
            insertWisely(state::class, graphEffects)
        }
    }

    private fun <R, S : Parcelable, E : Any> reduceEvents(vm: IBaseViewModel<R, S, E>,
                                                          event: BaseEvent<*>,
                                                          effect: Any,
                                                          graphEffects: MutableList<KClass<*>>) {
        val result = try {
            vm.reduceEventsToResults(event, effect).blockingFirst()
        } catch (exception: Exception) {
            return
        }
        if (result is SuccessEffectResult<*>) {
            graphEffects.add(result.bundle!!::class)
        }
    }

    private fun Graph.insertWisely(vertex: KClass<*>, neighbours: MutableList<KClass<*>>) {
        if (adjVertices.containsKey(vertex)) {
            adjVertices[vertex] = neighbours.plus(getAdjVerticesFor(vertex).asIterable())
        } else {
            adjVertices[vertex] = neighbours.toList()
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

