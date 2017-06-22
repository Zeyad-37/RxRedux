package com.zeyad.rxredux.core.redux;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatDelegate;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import com.zeyad.rxredux.core.eventbus.IRxEventBus;
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory;
import com.zeyad.rxredux.core.navigation.INavigator;
import com.zeyad.rxredux.core.navigation.NavigatorFactory;
import com.zeyad.rxredux.core.snackbar.SnackBarFactory;

import org.parceler.Parcels;

import java.util.List;

import io.reactivex.BackpressureStrategy;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;

/**
 * @author zeyad on 11/28/16.
 */
public abstract class BaseActivity<S, VM extends BaseViewModel<S>> extends RxAppCompatActivity
        implements LoadDataView<S> {
    public static final String UI_MODEL = "viewState";
    public INavigator navigator;
    public IRxEventBus rxEventBus;
    public ErrorMessageFactory errorMessageFactory;
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
        if (savedInstanceState != null) {
            viewState = Parcels.unwrap(savedInstanceState.getParcelable(UI_MODEL));
        }
        initialize();
        setupUI();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            viewState = Parcels.unwrap(savedInstanceState.getParcelable(UI_MODEL));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null) {
            outState.putAll(saveState());
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * To implement! Saves the viewState of the current view. Do not return null!
     *
     * @return {@link Bundle}
     */
    private Bundle saveState() {
        Bundle bundle = new Bundle(1);
        bundle.putParcelable(UI_MODEL, Parcels.wrap(viewState));
        return bundle;
    }

    @Override
    protected void onStart() {
        super.onStart();
        uiModelsTransformer = viewModel.uiModels();
        events.toFlowable(BackpressureStrategy.BUFFER)
                .compose(uiModelsTransformer)
                .compose(this.<UIModel<S>>bindToLifecycle())
                .subscribe(new UISubscriber<>(this, errorMessageFactory));
    }

    /**
     * Initialize objects or any required dependencies.
     */
    public abstract void initialize();

    /**
     * Setup the UI.
     */
    public abstract void setupUI();

    /**
     * Adds a {@link Fragment} to this activity's layout.
     *
     * @param containerViewId The container view to where add the fragment.
     * @param fragment        The fragment to be added.
     */
    public void addFragment(
            int containerViewId,
            Fragment fragment,
            String currentFragTag,
            List<Pair<View, String>> sharedElements) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (sharedElements != null) {
            for (Pair<View, String> pair : sharedElements) {
                fragmentTransaction.addSharedElement(pair.first, pair.second);
            }
        }
        if (currentFragTag == null || currentFragTag.isEmpty()) {
            fragmentTransaction.addToBackStack(fragment.getTag());
        } else {
            fragmentTransaction.addToBackStack(currentFragTag);
        }
        fragmentTransaction.add(containerViewId, fragment, fragment.getTag()).commit();
    }

    public void removeFragment(String tag) {
        getSupportFragmentManager()
                .beginTransaction()
                .remove(getSupportFragmentManager().findFragmentByTag(tag))
                .commit();
    }

    public void showToastMessage(String message) {
        showToastMessage(message, Toast.LENGTH_LONG);
    }

    public void showToastMessage(String message, int duration) {
        Toast.makeText(this, message, duration).show();
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
