package com.zeyad.rxredux.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Vertex(val neighbours: Array<KClass<*>> = [],
                        val results: Array<KClass<*>> = [],
                        val root: Boolean = false,
                        val leaf: Boolean = false)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class RootVertex(val neighbours: Array<KClass<*>> = [],
                            val results: Array<KClass<*>> = [],
                            val root: Boolean = false,
                            val leaf: Boolean = false)

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class LeafVertex(val neighbours: Array<KClass<*>> = [],
                            val results: Array<KClass<*>> = [],
                            val root: Boolean = false,
                            val leaf: Boolean = false)
