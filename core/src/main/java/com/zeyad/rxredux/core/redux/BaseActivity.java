package com.zeyad.rxredux.core.redux;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;

import com.zeyad.rxredux.core.eventbus.IRxEventBus;
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory;
import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;

import io.reactivex.BackpressureStrategy;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;

/**
 * @author Zeyad.
 */
public abstract class BaseActivity<S extends Parcelable, VM extends BaseViewModel<S>> extends Activity
        implements LoadDataView<S>, LifecycleOwner {

    public static final String UI_MODEL = "viewState";
    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public Observable<BaseEvent> events;
    public FlowableTransformer<BaseEvent, UIModel<S>> uiModelsTransformer;
    public VM viewModel;
    public S viewState;
    private LifecycleRegistry mLifecycleRegistry;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
        navigator = NavigatorFactory.getInstance();
        rxEventBus = RxEventBusFactory.getInstance();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        restoreViewStateFromBundle(savedInstanceState);
        events = Observable.empty();
        initialize();
        setupUI(savedInstanceState == null);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreViewStateFromBundle(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
        uiModelsTransformer = viewModel.uiModels();
        LiveDataReactiveStreams
                .fromPublisher(events.toFlowable(BackpressureStrategy.BUFFER).compose(uiModelsTransformer))
                .observe(this, new UIObserver<>(this, errorMessageFactory()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
    }

    @Override
    protected void onSaveInstanceState(@Nullable Bundle bundle) {
        if (bundle != null && viewState != null) {
            bundle.putParcelable(UI_MODEL, viewState);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    @Override
    public void setState(S bundle) {
        viewState = bundle;
    }

    private void restoreViewStateFromBundle(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL)) {
            viewState = savedInstanceState.getParcelable(UI_MODEL);
        }
    }

    @NonNull
    public abstract ErrorMessageFactory errorMessageFactory();

    /**
     * Initialize objects or any required dependencies.
     */
    public abstract void initialize();

    /**
     * Setup the UI.
     * @param isNew = savedInstanceState == null
     */
    public abstract void setupUI(boolean isNew);

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
