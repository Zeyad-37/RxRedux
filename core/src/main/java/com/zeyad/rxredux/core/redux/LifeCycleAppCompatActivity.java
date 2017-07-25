package com.zeyad.rxredux.core.redux;

import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LifecycleRegistryOwner;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

/**
 * @author by Zeyad.
 */
class LifeCycleAppCompatActivity extends AppCompatActivity implements LifecycleRegistryOwner {

    private final LifecycleRegistry mRegistry = new LifecycleRegistry(this);

    @NonNull
    @Override
    public LifecycleRegistry getLifecycle() {
        return mRegistry;
    }
}
