package com.zeyad.rxredux.core.redux;

import android.arch.lifecycle.LiveDataReactiveStreams;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;

import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;

import io.reactivex.Observable;

import static com.zeyad.rxredux.core.redux.BaseView.UI_MODEL;

/**
 * @author by Zeyad.
 */
public abstract class BaseFragmentActivity<S extends Parcelable, VM extends BaseViewModel<S>>
        extends FragmentActivity implements LoadDataView<S> {

    public INavigator navigator;
    public VM viewModel;
    public S viewState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        navigator = NavigatorFactory.getInstance();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        viewState = BaseView.getViewStateFrom(savedInstanceState, getIntent());
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
        LiveDataReactiveStreams.fromPublisher(viewModel.uiModels(viewState))
                .observe(this, new UIObserver<>(this, errorMessageFactory()));
        viewModel.processEvents(events());
    }

    @Override
    public void setState(S bundle) {
        viewState = bundle;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewState = BaseView.getViewStateFrom(savedInstanceState, getIntent());
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

    /**
     * Merge all events into one {@link Observable}.
     *
     * @return {@link Observable}.
     */
    public abstract Observable<BaseEvent> events();
}
