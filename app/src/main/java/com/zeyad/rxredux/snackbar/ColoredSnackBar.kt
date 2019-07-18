package com.zeyad.rxredux.snackbar

import android.view.View
import androidx.annotation.ColorInt
import com.google.android.material.snackbar.Snackbar

object ColoredSnackBar {

    private fun getSnackBarLayout(snackBar: Snackbar): View? = snackBar.view

    private fun colorSnackBar(snackBar: Snackbar, @ColorInt colorId: Int): Snackbar =
            snackBar.apply { getSnackBarLayout(snackBar)?.setBackgroundColor(colorId) }

    fun info(snackBar: Snackbar, color: Int): Snackbar = colorSnackBar(snackBar, color)

    fun error(snackBar: Snackbar, color: Int): Snackbar = colorSnackBar(snackBar, color)
}
