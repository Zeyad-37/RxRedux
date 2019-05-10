package com.zeyad.rxredux.core.viewmodel

import android.os.Build.VERSION_CODES.N
import android.os.Parcelable
import android.support.annotation.RequiresApi
import com.zeyad.rxredux.core.BaseEvent
import com.zeyad.rxredux.core.LeafVertex
import com.zeyad.rxredux.core.RootVertex
import java.util.*
import kotlin.collections.LinkedHashSet

class GraphVerifier<R, S : Parcelable, E : Any, VM : IBaseViewModel<R, S, E>>(private val vm: VM) {

    @RequiresApi(N)
    fun verify(events: List<BaseEvent<*>>, states: List<S>, effects: List<E>, results: List<R>): Boolean {
        return Graph().run {
            fill(events, states, effects, results)
            validate(states, effects)
        }
    }

    @RequiresApi(N)
    private fun Graph.fill(events: List<BaseEvent<*>>, states: List<S>, effects: List<E>, results: List<R>) {
        fillByReducingEventsWithStates(events, states)
        fillByReducingEventsWithEffects(events, effects)
        fillByReducingStatesWithResults(states, results)
    }

    @RequiresApi(N)
    private fun Graph.fillByReducingStatesWithResults(states: List<S>, results: List<R>) {
        val graphStates = mutableListOf<Vertex>()
        for (state in states) {
            for (result in results) {
                try {
                    graphStates.add(Vertex(vm.stateReducer(result, state)))
                } catch (exception: Exception) {
                    continue
                }
            }
            insertWisely(Vertex(state), graphStates)
        }
    }

    @RequiresApi(N)
    private fun Graph.fillByReducingEventsWithEffects(events: List<BaseEvent<*>>, effects: List<E>) {
        val graphEffects: MutableList<Vertex> = mutableListOf()
        for (effect in effects) {
            for (event in events) {
                reduceEvents(event, effect, graphEffects)
            }
            insertWisely(Vertex(effect), graphEffects)
        }
    }

    @RequiresApi(N)
    private fun Graph.fillByReducingEventsWithStates(events: List<BaseEvent<*>>, states: List<S>) {
        val graphEffects: MutableList<Vertex> = mutableListOf()
        for (state in states) {
            for (event in events) {
                reduceEvents(event, state, graphEffects)
            }
            insertWisely(Vertex(state), graphEffects)
        }
    }

    private fun reduceEvents(event: BaseEvent<*>, effect: Any, graphEffects: MutableList<Vertex>) {
        val result = try {
            vm.reduceEventsToResults(event, effect).blockingFirst()
        } catch (exception: Exception) {
            return
        }
        if (result is SuccessEffectResult<*>) {
            graphEffects.add(Vertex(result.bundle!!))
        }
    }

    @RequiresApi(N)
    private fun Graph.insertWisely(key: Vertex, graphEffects: MutableList<Vertex>) {
        if (adjVertices.containsKey(key)) {
            adjVertices.replace(key, graphEffects.plus(getAdjVerticesFor(key).asIterable()))
        } else {
            adjVertices.putIfAbsent(key, graphEffects.toList())
        }
        graphEffects.clear()
    }

    private fun Graph.validate(states: List<S>, effects: List<E>): Boolean {
        val roots = states.filter { it is RootVertex }.map { Vertex(it) }
        val leaves = states.filter { it is LeafVertex }.map { Vertex(it) }
                .plus(effects.filter { it is LeafVertex }.map { Vertex(it as Any) })
        return roots.all { validateCore(it, leaves) }
    }

    private fun Graph.validateCore(root: Vertex, leaves: List<Vertex>): Boolean {
        val calculatedLeaves =
                depthFirstTraversal(root).filter { it.state is LeafVertex }.toList()
        val leafTypes = leaves.map { it::class.java }
        val calculatedLeavesTypes = calculatedLeaves.map { it::class.java }
        return calculatedLeavesTypes.containsAll(leafTypes) && calculatedLeaves.size == leaves.size
    }
}

data class Vertex(val state: Any)

data class Graph(val adjVertices: MutableMap<Vertex, List<Vertex>> = mutableMapOf()) {

    fun depthFirstTraversal(root: Vertex): Set<Vertex> {
        val visited = LinkedHashSet<Vertex>()
        val stack = Stack<Vertex>()
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

    fun getAdjVerticesFor(key: Vertex): List<Vertex> {
        return try {
            adjVertices[key] ?: adjVertices[Vertex(key.state::class.java.newInstance())]!!
        } catch (e: Exception) {
            emptyList()
        }
    }
}
