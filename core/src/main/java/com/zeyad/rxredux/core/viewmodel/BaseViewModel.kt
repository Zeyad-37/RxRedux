package com.zeyad.rxredux.core.viewmodel

import android.arch.lifecycle.ViewModel

abstract class BaseViewModel<S> : IBaseViewModel<S>, ViewModel()
