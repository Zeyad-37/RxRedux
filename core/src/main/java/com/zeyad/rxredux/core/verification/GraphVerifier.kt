package com.zeyad.rxredux.core.verification

import android.os.Parcelable
import com.zeyad.rxredux.core.viewmodel.IBaseViewModel
import com.zeyad.rxredux.core.viewmodel.SuccessEffectResult
import kotlin.reflect.KClass

internal class GraphVerifier {

    fun <I, R, S : Parcelable, E : Any> verify(vm: IBaseViewModel<I, R, S, E>,
                                                              events: List<I>,
                                                              states: List<S>,
                                                              effects: List<E>,
                                                              results: List<R>): Boolean {
        return Graph().run {
            fill(vm, events, states, effects, results)
            validate(states, effects)
        }
    }

    private fun <I, R, S : Parcelable, E : Any> Graph.fill(vm: IBaseViewModel<I, R, S, E>,
                                                           events: List<I>,
                                                           states: List<S>,
                                                           effects: List<E>,
                                                           results: List<R>) {
        fillByReducingIntentsWithStates(vm, events, states)
        fillByReducingIntentsWithEffects(vm, events, effects)
        fillByReducingStatesWithResults(vm, states, results)
    }

    private fun <I, R, S : Parcelable, E : Any> Graph.fillByReducingStatesWithResults(vm: IBaseViewModel<I, R, S, E>,
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

    private fun <I, R, S : Parcelable, E : Any> Graph.fillByReducingIntentsWithEffects(vm: IBaseViewModel<I, R, S, E>,
                                                                                      events: List<I>,
                                                                                      effects: List<E>) {
        val graphEffects: MutableSet<KClass<*>> = mutableSetOf()
        for (effect in effects) {
            for (intent in events) {
                reduceIntents(vm, intent, effect, graphEffects)
            }
            insertWisely(effect::class, graphEffects)
        }
    }

    private fun <I, R, S : Parcelable, E : Any> Graph.fillByReducingIntentsWithStates(vm: IBaseViewModel<I, R, S, E>,
                                                                                     events: List<I>,
                                                                                     states: List<S>) {
        val graphEffects: MutableSet<KClass<*>> = mutableSetOf()
        for (state in states) {
            for (intent in events) {
                reduceIntents(vm, intent, state, graphEffects)
            }
            insertWisely(state::class, graphEffects)
        }
    }

    private fun <I, R, S : Parcelable, E : Any> reduceIntents(vm: IBaseViewModel<I, R, S, E>,
                                                             intent: I,
                                                             effect: Any,
                                                             graphEffects: MutableSet<KClass<*>>) {
        val result = try {
            vm.reduceIntentsToResults(intent, effect).blockingFirst()
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
