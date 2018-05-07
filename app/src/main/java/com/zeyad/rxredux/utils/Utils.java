package com.zeyad.rxredux.utils;

import android.os.Build;
import android.support.annotation.NonNull;

/**
 * @author by ZIaDo on 10/1/16.
 */
public class Utils {

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static <T> void runIfNotNull(@NonNull final T t, @NonNull Execute<T> execute) {
        execute.run(t);
    }

    public static <T, R> R runAndReturnIfNotNull(@NonNull final T t, @NonNull ExecuteAndReturn<T, R> execute) {
        return execute.run(t);
    }

    public interface Execute<T> {
        void run(T activity);
    }

    public interface ExecuteAndReturn<T, R> {
        R run(T activity);
    }
}
