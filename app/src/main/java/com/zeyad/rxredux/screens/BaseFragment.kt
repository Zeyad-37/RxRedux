package com.zeyad.rxredux.screens

import android.os.Parcelable
import android.view.View
import android.widget.Toast
import com.zeyad.rxredux.core.view.BaseFragment
import com.zeyad.rxredux.core.viewmodel.BaseViewModel
import com.zeyad.rxredux.snackbar.SnackBarFactory

abstract class BaseFragment<S : Parcelable, VM : BaseViewModel<S>> : BaseFragment<S, VM>() {

    @JvmOverloads
    fun showToastMessage(message: String, duration: Int = Toast.LENGTH_LONG) =
            Toast.makeText(context, message, duration).show()

    /**
     * Shows a [android.support.design.widget.Snackbar] message.
     *
     * @param message An string representing a message to be shown.
     */
    fun showSnackBarMessage(view: View, message: String, duration: Int) =
            SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_INFO, view, message, duration).show()

    fun showSnackBarWithAction(typeSnackBar: String,
                               view: View,
                               message: String,
                               actionText: String,
                               onClickListener: View.OnClickListener) =
            SnackBarFactory
                    .getSnackBarWithAction(typeSnackBar, view, message, actionText, onClickListener)
                    .show()

    fun showSnackBarWithAction(typeSnackBar: String,
                               view: View,
                               message: String,
                               actionText: Int,
                               onClickListener: View.OnClickListener) =
            showSnackBarWithAction(typeSnackBar, view, message, getString(actionText), onClickListener)

    /**
     * Shows a [android.support.design.widget.Snackbar] errorResult message.
     *
     * @param message  An string representing a message to be shown.
     * @param duration Visibility duration.
     */
    fun showErrorSnackBar(message: String, view: View, duration: Int) =
            SnackBarFactory.getSnackBar(SnackBarFactory.TYPE_ERROR, view, message, duration)
                    .show()
}
