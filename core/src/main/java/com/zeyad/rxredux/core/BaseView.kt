package com.zeyad.rxredux.core

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable

/**
 * @author ZIaDo on 2/27/18.
 */
class BaseView {

    companion object {
        const val UI_MODEL = "viewState"

        fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?, intent: Intent?): S? {
            var viewState: S? = null
            if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL)) {
                viewState = savedInstanceState.getParcelable(UI_MODEL)
            } else if (intent != null && intent.hasCategory(UI_MODEL)) {
                viewState = intent.getParcelableExtra(UI_MODEL)
            }
            return viewState
        }

        fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?, arguments: Bundle?): S? {
            var viewState: S? = null
            if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL)) {
                viewState = savedInstanceState.getParcelable(UI_MODEL)
            } else if (arguments != null && arguments.containsKey(UI_MODEL)) {
                viewState = arguments.getParcelable(UI_MODEL)
            }
            return viewState
        }
    }
}