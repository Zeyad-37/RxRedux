package com.zeyad.rxredux.snackbar

import android.graphics.Color
import android.support.annotation.ColorInt
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.design.widget.Snackbar.LENGTH_INDEFINITE
import android.view.View

object SnackBarFactory {

    val TYPE_INFO = "typeInfo"
    val TYPE_ERROR = "typeError"

    fun getSnackBar(snackBarType: String, view: View, @StringRes stringId: Int,
                    duration: Int): Snackbar {
        return getSnackBar(view, stringId, duration, getColorId(snackBarType))
    }

    fun getSnackBar(snackBarType: String, view: View, text: CharSequence,
                    duration: Int): Snackbar {
        return getSnackBar(view, text, duration, getColorId(snackBarType))
    }

    fun getSnackBar(view: View, @StringRes stringId: Int, duration: Int,
                    @ColorInt colorId: Int): Snackbar {
        return createSnackBar(Snackbar.make(view, stringId, duration), colorId)
    }

    fun getSnackBar(view: View, text: CharSequence, duration: Int,
                    @ColorInt colorId: Int): Snackbar {
        return createSnackBar(Snackbar.make(view, text, duration), colorId)
    }

    fun getSnackBarWithAction(snackBarType: String, view: View,
                              text: CharSequence, actionText: String?, onClickListener: View.OnClickListener): Snackbar {
        return createSnackBar(Snackbar.make(view, text, LENGTH_INDEFINITE)
                .setAction(if (actionText != null && !actionText.isEmpty()) actionText else "RETRY", onClickListener)
                .setActionTextColor(Color.BLACK), getColorId(snackBarType))
    }

    private fun createSnackBar(snackbar: Snackbar, @ColorInt colorId: Int): Snackbar {
        return ColoredSnackBar.info(snackbar, colorId)
    }

    private fun getColorId(snackBarType: String): Int {
        return if (snackBarType == TYPE_INFO) Color.parseColor("#45d482") else Color.parseColor("#e15D50")
    }
}
