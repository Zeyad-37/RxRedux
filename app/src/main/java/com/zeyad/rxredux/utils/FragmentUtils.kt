package com.zeyad.rxredux.utils

import android.support.v4.app.Fragment
import android.view.View
import android.widget.Toast
import com.zeyad.rxredux.snackbar.SnackBarFactory

@JvmOverloads
fun Fragment.showToastMessage(message: String, duration: Int = Toast.LENGTH_LONG) =
        Toast.makeText(context, message, duration).show()

/**
 * Shows a [android.support.design.widget.Snackbar] messageId.
 *
 * @param message An string representing a messageId to be shown.
 */
fun Fragment.showSnackBarMessage(view: View, message: String, duration: Int) =
        SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_INFO, view, message, duration).show()

fun Fragment.showSnackBarWithAction(typeSnackBar: String,
                                    view: View,
                                    message: String,
                                    actionText: String,
                                    onClickListener: View.OnClickListener) =
        SnackBarFactory
                .getSnackBarWithAction(typeSnackBar, view, message, actionText, onClickListener)
                .show()

fun Fragment.showSnackBarWithAction(typeSnackBar: String,
                                    view: View,
                                    message: String,
                                    actionText: Int,
                                    onClickListener: View.OnClickListener) =
        showSnackBarWithAction(typeSnackBar, view, message, getString(actionText), onClickListener)

/**
 * Shows a [android.support.design.widget.Snackbar] errorResult messageId.
 *
 * @param message  An string representing a messageId to be shown.
 * @param duration Visibility duration.
 */
fun Fragment.showErrorSnackBar(message: String, view: View, duration: Int) =
        SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_ERROR, view, message, duration)
                .show()
