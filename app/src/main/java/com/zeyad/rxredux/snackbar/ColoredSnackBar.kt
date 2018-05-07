package com.zeyad.rxredux.snackbar

import android.support.annotation.ColorInt
import android.support.design.widget.Snackbar
import android.view.View

/**
 * @author by ZIaDo on 7/21/17.
 */
object ColoredSnackBar {

    private fun getSnackBarLayout(snackbar: Snackbar): View? {
        return snackbar.view
    }

    private fun colorSnackBar(snackbar: Snackbar, @ColorInt colorId: Int): Snackbar {
        val snackBarView = getSnackBarLayout(snackbar)
        snackBarView?.setBackgroundColor(colorId)
        return snackbar
    }

    fun info(snackbar: Snackbar, color: Int): Snackbar {
        return colorSnackBar(snackbar, color)
    }

    fun error(snackbar: Snackbar, color: Int): Snackbar {
        return colorSnackBar(snackbar, color)
    }
}