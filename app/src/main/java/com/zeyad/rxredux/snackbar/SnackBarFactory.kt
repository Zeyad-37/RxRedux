package com.zeyad.rxredux.snackbar

import android.graphics.Color
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.Snackbar.LENGTH_INDEFINITE

object SnackBarFactory {

    const val TYPE_INFO = "typeInfo"
    const val TYPE_ERROR = "typeError"

    fun getSnackBar(snackBarType: String,
                    view: View,
                    @StringRes stringId: Int,
                    duration: Int): Snackbar =
            getSnackBar(view, stringId, duration, getColorId(snackBarType))

    fun getSnackBar(snackBarType: String, view: View, text: CharSequence,
                    duration: Int): Snackbar =
            getSnackBar(view, text, duration, getColorId(snackBarType))

    fun getSnackBar(view: View, @StringRes stringId: Int, duration: Int,
                    @ColorInt colorId: Int): Snackbar =
            createSnackBar(Snackbar.make(view, stringId, duration), colorId)

    fun getSnackBar(view: View, text: CharSequence, duration: Int,
                    @ColorInt colorId: Int): Snackbar =
            createSnackBar(Snackbar.make(view, text, duration), colorId)

    fun getSnackBarWithAction(snackBarType: String, view: View,
                              text: CharSequence, actionText: String?,
                              onClickListener: View.OnClickListener): Snackbar =
            createSnackBar(Snackbar.make(view, text, LENGTH_INDEFINITE)
                    .setAction(if (actionText != null && !actionText.isEmpty()) actionText else "RETRY", onClickListener)
                    .setActionTextColor(Color.BLACK), getColorId(snackBarType))

    private fun createSnackBar(snackBar: Snackbar, @ColorInt colorId: Int): Snackbar =
            ColoredSnackBar.info(snackBar, colorId)

    private fun getColorId(snackBarType: String): Int =
            Color.parseColor(if (snackBarType == TYPE_INFO) "#45d482" else "#e15D50")
}
