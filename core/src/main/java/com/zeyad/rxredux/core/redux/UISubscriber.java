package com.zeyad.rxredux.core.redux;

import android.util.Log;

import io.reactivex.annotations.NonNull;
import io.reactivex.exceptions.OnErrorNotImplementedException;
import io.reactivex.subscribers.DisposableSubscriber;

/**
 * @author Zeyad.
 */
public class UISubscriber<V extends LoadDataView<S>, S> extends DisposableSubscriber<UIModel<S>> {
    @NonNull
    private final ErrorMessageFactory errorMessageFactory;
    @NonNull
    private final V view;

    public UISubscriber(@NonNull V view, @NonNull ErrorMessageFactory errorMessageFactory) {
        this.view = view;
        this.errorMessageFactory = errorMessageFactory;
    }

    @Override
    public void onNext(@NonNull UIModel<S> uiModel) {
        Log.d("onNext", "UIModel: " + uiModel.toString());
        view.toggleViews(uiModel.isLoading());
        if (!uiModel.isLoading()) {
            if (uiModel.isSuccessful()) {
                view.renderSuccessState(uiModel.getBundle());
            } else if (uiModel.getError() != null) {
                Throwable throwable = uiModel.getError();
                Log.e("UISubscriber", "onNext", throwable);
                view.showError(errorMessageFactory.getErrorMessage(throwable));
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        throw new OnErrorNotImplementedException(throwable);
    }

    @Override
    public void onComplete() {
    }
}
