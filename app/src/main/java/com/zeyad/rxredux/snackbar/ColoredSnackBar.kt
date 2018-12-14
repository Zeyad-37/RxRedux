package com.zeyad.rxredux.snackbar

import android.support.annotation.ColorInt
import android.support.design.widget.Snackbar
import android.view.View

object ColoredSnackBar {

    private fun getSnackBarLayout(snackbar: Snackbar): View? {
        return snackbar.view
    }

    private fun colorSnackBar(snackBar: Snackbar, @ColorInt colorId: Int): Snackbar {
        val snackBarView = getSnackBarLayout(snackBar)
        snackBarView?.setBackgroundColor(colorId)
        return snackBar
    }

    fun info(snackBar: Snackbar, color: Int): Snackbar {
        return colorSnackBar(snackBar, color)
    }

    fun error(snackBar: Snackbar, color: Int): Snackbar {
        return colorSnackBar(snackBar, color)
    }
}