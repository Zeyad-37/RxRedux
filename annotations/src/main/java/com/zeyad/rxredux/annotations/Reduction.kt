package com.zeyad.rxredux.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Reduction(val from: KClass<*>, val to: KClass<*>, val on: KClass<*>)