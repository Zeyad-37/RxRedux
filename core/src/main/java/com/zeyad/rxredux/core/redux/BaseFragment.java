package com.zeyad.rxredux.core.redux;

import static com.zeyad.rxredux.core.redux.BaseActivity.UI_MODEL;

import org.parceler.Parcels;

import com.trello.rxlifecycle2.components.support.RxFragment;
import com.zeyad.rxredux.core.eventbus.IRxEventBus;
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory;
import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;

import android.os.Bundle;
import android.widget.Toast;

import io.reactivex.BackpressureStrategy;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseFragment<S, VM extends BaseViewModel<S>> extends RxFragment
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        navigator = NavigatorFactory.getInstance();
        rxEventBus = RxEventBusFactory.getInstance();
        if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL)) {
            viewState = Parcels.unwrap(savedInstanceState.getParcelable(UI_MODEL));
        }
        initialize();
    }

    @Override
    public void onStart() {
        super.onStart();
        uiModelsTransformer = viewModel.uiModels();
        events.toFlowable(BackpressureStrategy.BUFFER)
                .compose(uiModelsTransformer)
                .compose(this.<UIModel<S>> bindToLifecycle())
                .subscribe(new UISubscriber<>(this, errorMessageFactory()));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null && viewState != null) {
            outState.putParcelable(UI_MODEL, Parcels.wrap(viewState));
        }
        super.onSaveInstanceState(outState);
    }

    public abstract ErrorMessageFactory errorMessageFactory();

    /**
     * Initialize any objects or any required dependencies.
     */
    public abstract void initialize();

    public void showToastMessage(String message) {
        showToastMessage(message, Toast.LENGTH_LONG);
    }

    public void showToastMessage(String message, int duration) {
        Toast.makeText(getContext(), message, duration).show();
    }
}
