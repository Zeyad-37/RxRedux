package com.zeyad.rxredux.core.navigation;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public interface INavigator {
    void navigateTo(Context context, Intent intent);

    void navigateTo(Context context, Intent intent, ActivityOptions activityOptions);

    void startForResult(Activity activity, Intent intent, int requestCode);

    void startForResult(@NonNull Activity activity, Intent intent, int requestCode,
            @NonNull ActivityOptions activityOptions);
}
