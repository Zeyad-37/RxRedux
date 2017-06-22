package com.zeyad.rxredux.core.snackbar;

import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.view.View;

public final class ColoredSnackBar {
    private ColoredSnackBar() {
    }

    private static View getSnackBarLayout(Snackbar snackbar) {
        return snackbar != null ? snackbar.getView() : null;
    }

    private static Snackbar colorSnackBar(Snackbar snackbar, @ColorInt int colorId) {
        View snackBarView = getSnackBarLayout(snackbar);
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(colorId);
        }
        return snackbar;
    }

    public static Snackbar info(Snackbar snackbar, int color) {
        return colorSnackBar(snackbar, color);
    }

    public static Snackbar error(Snackbar snackbar, int color) {
        return colorSnackBar(snackbar, color);
    }
}
