package com.zeyad.rxredux.simplevm

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner
import com.zeyad.rxredux.core.vm.SimpleRxViewModel

@Suppress("UNCHECKED_CAST")
class MySimpleViewModelFactory(owner: SavedStateRegistryOwner, defaultArgs: Bundle?) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    override fun <T : ViewModel?> create(key: String, modelClass: Class<T>, handle: SavedStateHandle): T {
        val viewModel = MySimpleVm() as SimpleRxViewModel<*, *, *>
        viewModel.savedStateHandle = handle
        return viewModel as T
    }
}
