package com.zeyad.rxredux.core.navigation;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

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
        context.startActivity(intent);
    }

    @Override
    public void navigateTo(@NonNull Context context, Intent intent, @NonNull ActivityOptions activityOptions) {
        context.startActivity(intent, activityOptions.toBundle());
    }

    @Override
    public void startForResult(@NonNull Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }
}
