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

        fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?, intent: Intent?): S? =
                if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL)) {
                    savedInstanceState.getParcelable(UI_MODEL)
                } else if (intent != null && intent.hasCategory(UI_MODEL)) {
                    intent.getParcelableExtra(UI_MODEL)
                } else null

        fun <S : Parcelable> getViewStateFrom(savedInstanceState: Bundle?, arguments: Bundle?): S? =
                if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL)) {
                    savedInstanceState.getParcelable(UI_MODEL)
                } else if (arguments != null && arguments.containsKey(UI_MODEL)) {
                    arguments.getParcelable(UI_MODEL)
                } else null
    }
}

