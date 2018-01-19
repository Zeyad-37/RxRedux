package com.zeyad.rxredux.core.redux;

import android.app.Fragment;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.LiveDataReactiveStreams;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;

import io.reactivex.Observable;

import static com.zeyad.rxredux.core.redux.BaseView.UI_MODEL;

/**
 * @author Zeyad.
 */
public abstract class BaseFragment<S extends Parcelable, VM extends BaseViewModel<S>> extends Fragment
        implements LoadDataView<S>, LifecycleOwner {

    public INavigator navigator;
    public VM viewModel;
    public S viewState;
    private LifecycleRegistry mLifecycleRegistry;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLifecycleRegistry = new LifecycleRegistry(this);
        mLifecycleRegistry.markState(Lifecycle.State.CREATED);
        setRetainInstance(true);
        navigator = NavigatorFactory.getInstance();
        viewState = BaseView.getViewStateFrom(savedInstanceState, getArguments());
        initialize();
    }

    @Override
    public void onStart() {
        super.onStart();
        mLifecycleRegistry.markState(Lifecycle.State.STARTED);
        LiveDataReactiveStreams.fromPublisher(viewModel.uiModels(viewState))
                .observe(this, new UIObserver<>(this, errorMessageFactory()));
        viewModel.processEvents(events());
    }

    @Override
    public void onResume() {
        super.onResume();
        mLifecycleRegistry.markState(Lifecycle.State.RESUMED);
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle outState) {
        if (outState != null && viewState != null) {
            outState.putParcelable(UI_MODEL, viewState);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLifecycleRegistry.markState(Lifecycle.State.DESTROYED);
    }

    @Override
    public void setState(S bundle) {
        viewState = bundle;
    }

    @NonNull
    public abstract ErrorMessageFactory errorMessageFactory();

    /**
     * Initialize any objects or any required dependencies.
     */
    public abstract void initialize();

    /**
     * Merge all events into one {@link Observable}.
     *
     * @return {@link Observable}.
     */
    public abstract Observable<BaseEvent> events();

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return mLifecycleRegistry;
    }
}
