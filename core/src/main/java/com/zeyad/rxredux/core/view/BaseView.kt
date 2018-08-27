package com.zeyad.rxredux.core.view

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import android.os.Bundle
import android.os.Parcelable
import org.reactivestreams.Publisher

/**
 * @author Zeyad Gasser.
 */
const val UI_MODEL = "viewState"

fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?): S? =
        if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL))
            savedInstanceState.getParcelable(UI_MODEL)
        else null

fun <T> Publisher<T>.toLiveData() = LiveDataReactiveStreams.fromPublisher(this) as LiveData<T>
