package com.zeyad.rxredux.annotations

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class Transition(val results: Array<KClass<*>> = [])
