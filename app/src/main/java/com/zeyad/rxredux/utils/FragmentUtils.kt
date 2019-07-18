package com.zeyad.rxredux.utils

import android.view.View
import android.widget.Toast
import com.zeyad.rxredux.snackbar.SnackBarFactory

@JvmOverloads
fun androidx.fragment.app.Fragment.showToastMessage(message: String, duration: Int = Toast.LENGTH_LONG) =
        Toast.makeText(context, message, duration).show()

/**
 * Shows a [android.support.design.widget.Snackbar] messageId.
 *
 * @param message An string representing a messageId to be shown.
 */
fun androidx.fragment.app.Fragment.showSnackBarMessage(view: View, message: String, duration: Int) =
        SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_INFO, view, message, duration).show()

fun androidx.fragment.app.Fragment.showSnackBarWithAction(typeSnackBar: String,
                                                          view: View,
                                                          message: String,
                                                          actionText: String,
                                                          onClickListener: View.OnClickListener) =
        SnackBarFactory
                .getSnackBarWithAction(typeSnackBar, view, message, actionText, onClickListener)
                .show()

fun androidx.fragment.app.Fragment.showSnackBarWithAction(typeSnackBar: String,
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
fun androidx.fragment.app.Fragment.showErrorSnackBar(message: String, view: View, duration: Int) =
        SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_ERROR, view, message, duration)
                .show()
