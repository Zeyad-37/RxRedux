package com.zeyad.rxredux.utils;

import android.os.Build;
import android.support.annotation.NonNull;

import java.util.List;

/** @author by ZIaDo on 10/1/16. */
public class Utils {

    private Utils() {
    }

    public static boolean isNotEmpty(String text) {
        return text != null && !text.isEmpty() && !text.equalsIgnoreCase("null");
    }

    public static boolean isNotEmpty(List list) {
        return list != null && !list.isEmpty();
    }

    public static <T> List union(List<T> first, List<T> last) {
        if (isNotEmpty(first) && isNotEmpty(last)) {
            first.addAll(last);
            return first;
        } else if (isNotEmpty(first) && !isNotEmpty(last)) {
            return first;
        }
        return last;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean hasM() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }

    public static <T> void runIfNotNull(final T t, @NonNull Execute<T> execute) {
        if (t != null) {
            execute.run(t);
        }
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
