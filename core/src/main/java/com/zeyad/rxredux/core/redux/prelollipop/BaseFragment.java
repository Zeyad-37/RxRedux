package com.zeyad.rxredux.core.redux.prelollipop;

import android.arch.lifecycle.LiveDataReactiveStreams;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;
import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.BaseView;
import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.rxredux.core.redux.ErrorMessageFactory;
import com.zeyad.rxredux.core.redux.LoadDataView;
import com.zeyad.rxredux.core.redux.UIObserver;

import io.reactivex.Observable;

import static com.zeyad.rxredux.core.redux.BaseView.UI_MODEL;

/**
 * @author Zeyad.
 */
public abstract class BaseFragment<S extends Parcelable, VM extends BaseViewModel<S>> extends Fragment
        implements LoadDataView<S> {

    public INavigator navigator;
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
        viewState = BaseView.getViewStateFrom(savedInstanceState, getArguments());
        initialize();
    }

    @Override
    public void onStart() {
        super.onStart();
        LiveDataReactiveStreams.fromPublisher(viewModel.uiModels(viewState))
                .observe(this, new UIObserver<>(this, errorMessageFactory()));
        viewModel.processEvents(events());
    }

    @Override
    public void onSaveInstanceState(@Nullable Bundle outState) {
        if (outState != null && viewState != null) {
            outState.putParcelable(UI_MODEL, viewState);
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

    /**
     * Merge all events into one {@link Observable}.
     *
     * @return {@link Observable}.
     */
    public abstract Observable<BaseEvent> events();
}
