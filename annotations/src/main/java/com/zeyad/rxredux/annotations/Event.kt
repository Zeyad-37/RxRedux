package com.zeyad.rxredux.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class Event(val from: Array<KClass<*>>,
                       val results: Array<KClass<*>>)
