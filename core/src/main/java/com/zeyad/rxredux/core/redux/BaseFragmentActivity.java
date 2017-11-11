package com.zeyad.rxredux.core.redux;

import com.zeyad.rxredux.core.eventbus.IRxEventBus;
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory;
import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;

import android.arch.lifecycle.LiveDataReactiveStreams;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;

import io.reactivex.BackpressureStrategy;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;

/**
 * @author by Zeyad.
 */
public abstract class BaseFragmentActivity<S extends Parcelable, VM extends BaseViewModel<S>>
        extends FragmentActivity
        implements LoadDataView<S> {
    public static final String UI_MODEL = "viewState";
    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public Observable<BaseEvent> events;
    public FlowableTransformer<BaseEvent, UIModel<S>> uiModelsTransformer;
    public VM viewModel;
    public S viewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = NavigatorFactory.getInstance();
        rxEventBus = RxEventBusFactory.getInstance();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        restoreViewStateFromBundle(savedInstanceState);
        events = Observable.empty();
        initialize();
        setupUI(savedInstanceState == null);
    }

    @Override
    protected void onSaveInstanceState(@Nullable Bundle bundle) {
        if (bundle != null && viewState != null) {
            bundle.putParcelable(UI_MODEL, viewState);
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onStart() {
        super.onStart();
        uiModelsTransformer = viewModel.uiModels();
        LiveDataReactiveStreams
                .fromPublisher(events.toFlowable(BackpressureStrategy.BUFFER).compose(uiModelsTransformer))
                .observe(this, new UIObserver<>(this, errorMessageFactory()));
    }

    @Override
    public void setState(S bundle) {
        viewState = bundle;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreViewStateFromBundle(savedInstanceState);
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
}
