package com.zeyad.rxredux

import android.app.Application
import android.os.Looper
import android.os.StrictMode
import com.zeyad.rxredux.di.myModule
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.rx.RealmObservableFactory
import org.koin.android.ext.android.startKoin

class RxReduxApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeRealm()
        startKoin(this, listOf(myModule))
        RxAndroidPlugins.setMainThreadSchedulerHandler { AndroidSchedulers.from(Looper.getMainLooper(), true) }
        //        initializeStrictMode();
        //        Completable.fromAction(() -> {
        //            if (!checkAppTampering(this)) {
        //                throw new IllegalAccessException("App might be tampered with!");
        //            }
        //            initializeFlowUp();
        //            Rollbar.init(this, "c8c8b4cb1d4f4650a77ae1558865ca87", BuildConfig.DEBUG ? "debug" : "production");
        //        }).subscribeOn(Schedulers.io())
        //                   .subscribe(() -> {
        //                   }, Throwable::printStackTrace);
    }

    private fun initializeRealm() {
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder()
                .name("app.realm")
                .modules(Realm.getDefaultModule()!!, LibraryModule())
                .rxFactory(RealmObservableFactory())
                .deleteRealmIfMigrationNeeded()
                .build())
    }

    private fun initializeStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
        }
    }
}
