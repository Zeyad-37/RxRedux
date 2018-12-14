package com.zeyad.rxredux.utils

import android.os.Build

fun hasLollipop(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
}
