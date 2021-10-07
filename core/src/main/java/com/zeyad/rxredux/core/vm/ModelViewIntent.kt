package com.zeyad.rxredux.core.vm

import androidx.lifecycle.LifecycleOwner
import com.zeyad.rxredux.core.vm.rxvm.Input
import com.zeyad.rxredux.core.vm.rxvm.State
import com.zeyad.rxredux.core.vm.rxvm.ViewModelListener
import com.zeyad.rxredux.core.vm.rxvm.ViewModelListenerHelper
import io.reactivex.Flowable
import io.reactivex.Observable

interface ModelViewIntent<I : Input, S : State> {

    fun bind(initialState: S, inputs: () -> Observable<I> = { Observable.empty() }): ModelViewIntent<I, S>

    fun observe(lifecycleOwner: LifecycleOwner, init: ViewModelListenerHelper.() -> Unit)

    fun observe(lifecycleOwner: LifecycleOwner, viewModelListener: ViewModelListener)

    fun offer(input: I, inputStrategy: InputStrategy = InputStrategy.NONE)

    fun handleInputs(input: I): Flowable<BaseRxViewModel.RxOutcome>
}