package com.zeyad.rxredux.core.navigation;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.internal.operators.completable.CompletableFromAction;

/**
 * Class used to navigate through the application.
 */
final class Navigator implements INavigator {

    private static INavigator mInstance;

    static INavigator getInstance() {
        if (mInstance == null) {
            mInstance = new Navigator();
        }
        return mInstance;
    }

    @Override
    public void navigateTo(@NonNull Context context, Intent intent) {
        navigateOnMainThread(() -> context.startActivity(intent));
    }

    @Override
    public void navigateTo(@NonNull Context context, Intent intent, @NonNull ActivityOptions activityOptions) {
        navigateOnMainThread(() -> context.startActivity(intent, activityOptions.toBundle()));
    }

    @Override
    public void startForResult(@NonNull Activity activity, Intent intent, int requestCode) {
        navigateOnMainThread(() -> activity.startActivityForResult(intent, requestCode));
    }

    @Override
    public void startForResult(@NonNull Activity activity, Intent intent, int requestCode,
            @NonNull ActivityOptions activityOptions) {
        navigateOnMainThread(
                () -> activity.startActivityForResult(intent, requestCode, activityOptions.toBundle()));
    }

    private void navigateOnMainThread(final Navigate navigate) {
        CompletableFromAction.fromAction(navigate::run).subscribeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    private interface Navigate {
        void run();
    }
}
