package com.zeyad.rxredux.core.redux;

import org.parceler.Parcels;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.zeyad.rxredux.core.eventbus.IRxEventBus;
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory;
import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDelegate;

import io.reactivex.BackpressureStrategy;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;

/**
 * @author Zeyad.
 */
public abstract class BaseActivity<S, VM extends BaseViewModel<S>> extends RxAppCompatActivity
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
        initialize();
        setupUI();
    }

    @Override
    protected void onStart() {
        super.onStart();
        uiModelsTransformer = viewModel.uiModels();
        events.toFlowable(BackpressureStrategy.BUFFER).compose(uiModelsTransformer)
                .compose(this.<UIModel<S>> bindToLifecycle())
                .subscribe(new UISubscriber<>(this, errorMessageFactory()));
    }

    @Override
    protected void onSaveInstanceState(@Nullable Bundle bundle) {
        if (bundle != null && viewState != null) {
            bundle.putParcelable(UI_MODEL, Parcels.wrap(viewState));
        }
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        restoreViewStateFromBundle(savedInstanceState);
    }

    private void restoreViewStateFromBundle(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL)) {
            viewState = Parcels.unwrap(savedInstanceState.getParcelable(UI_MODEL));
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
     */
    public abstract void setupUI();
}
