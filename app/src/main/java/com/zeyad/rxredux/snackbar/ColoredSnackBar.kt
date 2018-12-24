package com.zeyad.rxredux.snackbar

import android.support.annotation.ColorInt
import android.support.design.widget.Snackbar
import android.view.View

object ColoredSnackBar {

    private fun getSnackBarLayout(snackBar: Snackbar): View? = snackBar.view

    private fun colorSnackBar(snackBar: Snackbar, @ColorInt colorId: Int): Snackbar =
            getSnackBarLayout(snackBar)?.apply { setBackgroundColor(colorId) } as Snackbar

    fun info(snackBar: Snackbar, color: Int): Snackbar = colorSnackBar(snackBar, color)

    fun error(snackBar: Snackbar, color: Int): Snackbar = colorSnackBar(snackBar, color)
}