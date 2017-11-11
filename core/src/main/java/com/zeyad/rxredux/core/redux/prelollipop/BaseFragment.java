package com.zeyad.rxredux.core.redux.prelollipop;

import com.zeyad.rxredux.core.eventbus.IRxEventBus;
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory;
import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;
import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.rxredux.core.redux.ErrorMessageFactory;
import com.zeyad.rxredux.core.redux.LoadDataView;
import com.zeyad.rxredux.core.redux.UIModel;
import com.zeyad.rxredux.core.redux.UIObserver;

import android.arch.lifecycle.LiveDataReactiveStreams;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import io.reactivex.BackpressureStrategy;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;

/**
 * @author Zeyad.
 */
public abstract class BaseFragment<S extends Parcelable, VM extends BaseViewModel<S>> extends Fragment
        implements LoadDataView<S> {

    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public Observable<BaseEvent> events;
    public FlowableTransformer<BaseEvent, UIModel<S>> uiModelsTransformer;
    public VM viewModel;
    public S viewState;

    public BaseFragment() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        navigator = NavigatorFactory.getInstance();
        rxEventBus = RxEventBusFactory.getInstance();
        if (savedInstanceState != null
                && savedInstanceState.containsKey(com.zeyad.rxredux.core.redux.BaseActivity.UI_MODEL)) {
            viewState = savedInstanceState.getParcelable(com.zeyad.rxredux.core.redux.BaseActivity.UI_MODEL);
        }
        events = Observable.empty();
        initialize();
    }

    @Override
    public void onStart() {
        super.onStart();
        uiModelsTransformer = viewModel.uiModels();
        LiveDataReactiveStreams
                .fromPublisher(events.toFlowable(BackpressureStrategy.BUFFER).compose(uiModelsTransformer))
                .observe(this, new UIObserver<>(this, errorMessageFactory()));
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle outState) {
        if (outState != null && viewState != null) {
            outState.putParcelable(com.zeyad.rxredux.core.redux.BaseActivity.UI_MODEL, viewState);
        }
        super.onSaveInstanceState(outState);
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
}

