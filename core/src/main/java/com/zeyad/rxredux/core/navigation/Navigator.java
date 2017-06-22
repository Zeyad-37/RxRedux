package com.zeyad.rxredux.core.navigation;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;

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
    public void navigateTo(Context context, Intent intent) {
        context.startActivity(intent);
    }

    @Override
    public void navigateTo(Context context, Intent intent, ActivityOptions activityOptions) {
        context.startActivity(intent, activityOptions.toBundle());
    }

    @Override
    public void startForResult(Activity activity, Intent intent, int requestCode) {
        activity.startActivityForResult(intent, requestCode);
    }
}
