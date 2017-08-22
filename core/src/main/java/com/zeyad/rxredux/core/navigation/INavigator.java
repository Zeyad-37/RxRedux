package com.zeyad.rxredux.core.navigation;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

public interface INavigator {
    void navigateTo(@NonNull Context context, @NonNull Intent intent);

    void navigateTo(@NonNull Context context, @NonNull Intent intent, @NonNull ActivityOptions activityOptions);

    void startForResult(@NonNull Activity activity, @NonNull Intent intent, int requestCode);

    void startForResult(@NonNull Activity activity, @NonNull Intent intent, int requestCode,
            @NonNull ActivityOptions activityOptions);

    void startService(@NonNull Context context, @NonNull Intent intent);
}
