package com.zeyad.rxredux.core.navigator

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * @author ZIaDo on 2/26/18.
 */
fun Context.navigate(intent: Intent, activityOptions: ActivityOptions? = null) {
    runOnMainThread { startActivity(intent, activityOptions?.toBundle()) }
}

fun Activity.forResult(intent: Intent, requestCode: Int) {
    runOnMainThread { startActivityForResult(intent, requestCode) }
}

fun Context.runService(intent: Intent) {
    runOnMainThread { startService(intent) }
}

@SuppressLint("RxSubscribeOnError")
private fun runOnMainThread(block: () -> Unit) {
    Completable.fromAction { block() }.subscribeOn(AndroidSchedulers.mainThread())
            .onTerminateDetach().subscribe().dispose()
}
