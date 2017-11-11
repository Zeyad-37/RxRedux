package com.zeyad.rxredux.core.redux;

import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.util.Log;

import io.reactivex.annotations.NonNull;

/**
 * @author by ZIaDo on 11/11/17.
 */
public class UIObserver<V extends LoadDataView<S>, S> implements Observer<UIModel<S>> {
    @NonNull
    private final V view;
    @NonNull
    private final ErrorMessageFactory errorMessageFactory;

    public UIObserver(@NonNull V view, @NonNull ErrorMessageFactory errorMessageFactory) {
        this.view = view;
        this.errorMessageFactory = errorMessageFactory;
    }

    @Override
    public void onChanged(@Nullable UIModel<S> uiModel) {
        Log.d("onNext", "UIModel: " + uiModel.toString());
        boolean loading = uiModel.isLoading();
        view.toggleViews(loading);
        if (!loading) {
            if (uiModel.isSuccessful()) {
                S bundle = uiModel.getBundle();
                view.setState(bundle);
                view.renderSuccessState(bundle);
            } else {
                Throwable error = uiModel.getError();
                if (error != null) {
                    Log.e("UISubscriber", "onNext", error);
                    view.showError(errorMessageFactory.getErrorMessage(error));
                }
            }
        }
    }
}
