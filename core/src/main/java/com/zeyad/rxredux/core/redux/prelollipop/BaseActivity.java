package com.zeyad.rxredux.core.redux.prelollipop;

import android.arch.lifecycle.LiveDataReactiveStreams;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;

import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;
import com.zeyad.rxredux.core.redux.BaseEvent;
import com.zeyad.rxredux.core.redux.BaseViewModel;
import com.zeyad.rxredux.core.redux.ErrorMessageFactory;
import com.zeyad.rxredux.core.redux.LoadDataView;
import com.zeyad.rxredux.core.redux.UIObserver;

import io.reactivex.Observable;

/**
 * @author Zeyad.
 */
public abstract class BaseActivity<S extends Parcelable, VM extends BaseViewModel<S>> extends AppCompatActivity
        implements LoadDataView<S> {
    public static final String UI_MODEL = "viewState";
    public INavigator navigator;
    public VM viewModel;
    public S viewState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = NavigatorFactory.getInstance();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        restoreViewStateFromBundle(savedInstanceState);
        initialize();
        setupUI(savedInstanceState == null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LiveDataReactiveStreams.fromPublisher(viewModel.uiModels(viewState))
                .observe(this, new UIObserver<>(this, errorMessageFactory()));
        viewModel.processEvents(events());
    }

    @Override
    protected void onSaveInstanceState(@Nullable Bundle bundle) {
        if (bundle != null && viewState != null) {
            bundle.putParcelable(UI_MODEL, viewState);
        }
        super.onSaveInstanceState(bundle);
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
     *
     * @param isNew = savedInstanceState == null
     */
    public abstract void setupUI(boolean isNew);

    /**
     * Merge all events into one {@link Observable}.
     *
     * @return {@link Observable}.
     */
    public abstract Observable<BaseEvent> events();
}
