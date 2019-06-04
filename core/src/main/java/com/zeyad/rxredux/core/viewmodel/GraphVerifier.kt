package com.zeyad.rxredux.core.viewmodel

import android.os.Build.VERSION_CODES.N
import android.os.Parcelable
import android.support.annotation.RequiresApi
import com.zeyad.rxredux.annotations.LeafVertex
import com.zeyad.rxredux.annotations.RootVertex
import com.zeyad.rxredux.core.BaseEvent
import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.reflect.KClass

class GraphVerifier {

    @RequiresApi(N)
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

    @RequiresApi(N)
    private fun <R, S : Parcelable, E : Any> Graph.fill(vm: IBaseViewModel<R, S, E>,
                                                        events: List<BaseEvent<*>>,
                                                        states: List<S>,
                                                        effects: List<E>,
                                                        results: List<R>) {
        fillByReducingEventsWithStates(vm, events, states)
        fillByReducingEventsWithEffects(vm, events, effects)
        fillByReducingStatesWithResults(vm, states, results)
    }

    @RequiresApi(N)
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

    @RequiresApi(N)
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

    @RequiresApi(N)
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

    @RequiresApi(N)
    private fun Graph.insertWisely(vertex: KClass<*>, neighbours: MutableList<KClass<*>>) {
        if (adjVertices.containsKey(vertex)) {
            adjVertices.replace(vertex, neighbours.plus(getAdjVerticesFor(vertex).asIterable()))
        } else {
            adjVertices.putIfAbsent(vertex, neighbours.toList())
        }
        neighbours.clear()
    }

    private fun <S : Parcelable, E : Any> Graph.validate(states: List<S>, effects: List<E>): Boolean {
        val roots =
                states.filter { it.javaClass.isAnnotationPresent(RootVertex::class.java) }.map { it::class }
        val leaves =
                states.filter { it.javaClass.isAnnotationPresent(LeafVertex::class.java) }.map { it::class }
                .plus(effects.filter { it.javaClass.isAnnotationPresent(LeafVertex::class.java) }.map { it::class })
        return roots.all { validateCore(it, leaves) }
    }

    private fun Graph.validateCore(root: KClass<*>, leaves: List<KClass<*>>): Boolean {
        val calculatedLeaves =
                depthFirstTraversal(root).filter { it.java.isAnnotationPresent(LeafVertex::class.java) }.toList()
        return calculatedLeaves.containsAll(leaves) && calculatedLeaves.size == leaves.size
    }
}

data class Graph(val adjVertices: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()) {

    fun depthFirstTraversal(root: KClass<*>): Set<KClass<*>> {
        val visited = LinkedHashSet<KClass<*>>()
        val stack = Stack<KClass<*>>()
        stack.push(root)
        while (!stack.isEmpty()) {
            val vertex = stack.pop()
            if (!visited.contains(vertex)) {
                visited.add(vertex)
                val adjList = getAdjVerticesFor(vertex)
                for (v in adjList) {
                    stack.push(v)
                }
            }
        }
        return visited
    }

    fun getAdjVerticesFor(key: KClass<*>): List<KClass<*>> {
        return try {
            adjVertices[key]!!
        } catch (e: Exception) {
            emptyList()
        }
    }
}
