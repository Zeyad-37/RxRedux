package com.zeyad.rxredux

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.zeyad.rxredux.core.vm.RxViewModel

@Suppress("UNCHECKED_CAST")
class MyViewModelFactory(owner: SavedStateRegistryOwner, defaultArgs: Bundle?) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        val viewModel = MyVm() as RxViewModel<*, *, *, *>
        viewModel.savedStateHandle = handle
        return viewModel as T
    }
}
