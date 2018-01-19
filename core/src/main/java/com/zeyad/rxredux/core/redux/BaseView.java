package com.zeyad.rxredux.core.redux;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

/**
 * @author ZIaDo on 1/19/18.
 */
public class BaseView {

    public static final String UI_MODEL = "viewState";

    private BaseView() {
    }

    public static <S extends Parcelable> S getViewStateFrom(Bundle savedInstanceState, Intent intent) {
        S viewState = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL)) {
            viewState = savedInstanceState.getParcelable(UI_MODEL);
        } else if (intent != null && intent.hasCategory(UI_MODEL)) {
            viewState = intent.getParcelableExtra(UI_MODEL);
        }
        return viewState;
    }

    public static <S extends Parcelable> S getViewStateFrom(Bundle savedInstanceState, Bundle arguments) {
        S viewState = null;
        if (savedInstanceState != null && savedInstanceState.containsKey(UI_MODEL)) {
            viewState = savedInstanceState.getParcelable(UI_MODEL);
        } else if (arguments != null && arguments.containsKey(UI_MODEL)) {
            viewState = arguments.getParcelable(UI_MODEL);
        }
        return viewState;
    }
}
