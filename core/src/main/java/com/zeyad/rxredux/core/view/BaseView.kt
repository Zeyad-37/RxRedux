package com.zeyad.rxredux.core.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.Bundle
import com.zeyad.rxredux.core.BaseEvent
import org.reactivestreams.Publisher

/**
 * @author Zeyad Gasser.
 */
const val UI_EVENT = "stateEvent"

fun Bundle.getLastStateEvent(): BaseEvent<*>? = getParcelable(UI_EVENT)

fun <T> Publisher<T>.toLiveData() = LiveDataReactiveStreams.fromPublisher(this) as LiveData<T>
