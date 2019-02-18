package com.zeyad.rxredux.core.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

abstract class BaseAndroidViewModel<S>(application: Application) : AndroidViewModel(application), IBaseViewModel<S>