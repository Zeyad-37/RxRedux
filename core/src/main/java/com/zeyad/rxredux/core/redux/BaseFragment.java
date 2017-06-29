package com.zeyad.rxredux.core.redux;

import static com.zeyad.rxredux.core.redux.BaseActivity.UI_MODEL;

import org.parceler.Parcels;

import com.zeyad.rxredux.core.eventbus.IRxEventBus;
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory;
import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;
import com.zeyad.rxredux.core.snackbar.SnackBarFactory;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import io.reactivex.BackpressureStrategy;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseFragment<S, VM extends BaseViewModel<S>> extends LifecycleFragment //RxFragment
        implements LoadDataView<S> {

    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public ErrorMessageFactory errorMessageFactory;
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
                //                .compose(this.<UIModel<S>>bindToLifecycle())
                .compose(LifecycleRxJavaBinder.<UIModel<S>> applyFlowable(this))
                .subscribe(new UISubscriber<>(this, errorMessageFactory));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState != null && viewState != null) {
            outState.putParcelable(UI_MODEL, Parcels.wrap(viewState));
        }
        super.onSaveInstanceState(outState);
    }

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

    /**
     * Shows a {@link android.support.design.widget.Snackbar} message.
     *
     * @param message An string representing a message to be shown.
     */
    public void showSnackBarMessage(View view, String message, int duration) {
        if (view != null) {
            SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_INFO, view, message, duration).show();
        } else {
            throw new IllegalArgumentException("View is null");
        }
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       String message, String actionText, View.OnClickListener onClickListener) {
        if (view != null) {
            SnackBarFactory.getSnackBarWithAction(
                    typeSnackBar, view, message, actionText, onClickListener)
                    .show();
        } else {
            throw new IllegalArgumentException("View is null");
        }
    }

    public void showSnackBarWithAction(@SnackBarFactory.SnackBarType String typeSnackBar, View view,
                                       String message, int actionText, View.OnClickListener onClickListener) {
        showSnackBarWithAction(typeSnackBar, view, message, getString(actionText), onClickListener);
    }

    /**
     * Shows a {@link android.support.design.widget.Snackbar} errorResult message.
     *
     * @param message  An string representing a message to be shown.
     * @param duration Visibility duration.
     */
    public void showErrorSnackBar(String message, View view, int duration) {
        if (view != null) {
            SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_ERROR, view, message, duration).show();
        } else {
            throw new IllegalArgumentException("View is null");
        }
    }
}
