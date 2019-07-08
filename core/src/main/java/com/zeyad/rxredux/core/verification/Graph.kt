package com.zeyad.rxredux.core.verification

import java.util.*
import kotlin.collections.LinkedHashSet
import kotlin.reflect.KClass

interface Root
interface Leaf

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