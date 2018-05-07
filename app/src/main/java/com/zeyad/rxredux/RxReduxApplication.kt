package com.zeyad.rxredux

import android.app.Application
import android.os.StrictMode
import android.util.Log
import com.zeyad.rxredux.core.eventbus.RxEventBusFactory
import com.zeyad.rxredux.utils.Constants.URLS.API_BASE_URL
import com.zeyad.usecases.api.DataServiceConfig
import com.zeyad.usecases.api.DataServiceFactory
import com.zeyad.usecases.network.ProgressInterceptor
import io.reactivex.BackpressureStrategy
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.rx.RealmObservableFactory
import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * @author by Zeyad on 7/27/17.
 */
class RxReduxApplication : Application() {

    internal//        if (getSSlSocketFactory() != null && getX509TrustManager() != null) {
    //            builder.sslSocketFactory(getSSlSocketFactory(), getX509TrustManager());
    //        }
    val okHttpBuilder: OkHttpClient.Builder
        get() = OkHttpClient.Builder().addInterceptor(object : ProgressInterceptor(
                { bytesRead, contentLength, done -> RxEventBusFactory.getInstance(BackpressureStrategy.BUFFER).send(-1) }) {
            override fun isFileIO(originalResponse: Response): Boolean {
                return false
            }
        }).addInterceptor(HttpLoggingInterceptor { message -> Log.d("NetworkInfo", message) }.setLevel(
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE))
                .connectTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS).readTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT.toLong(), TimeUnit.SECONDS)
                .certificatePinner(CertificatePinner.Builder()
                        .add(API_BASE_URL, "sha256/6wJsqVDF8K19zxfLxV5DGRneLyzso9adVdUN/exDacw")
                        .add(API_BASE_URL, "sha256/k2v657xBsOVe1PQRwOsHsw3bsGT2VzIqz5K+59sNQws=")
                        .add(API_BASE_URL, "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=").build())
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS))

    internal val apiBaseUrl: String
        get() = API_BASE_URL

    override fun onCreate() {
        super.onCreate()
        //        if (LeakCanary.isInAnalyzerProcess(this)) {
        //            return;
        //        }
        //        initializeStrictMode();
        //        LeakCanary.install(this);
        //        Completable.fromAction(() -> {
        //            if (!checkAppTampering(this)) {
        //                throw new IllegalAccessException("App might be tampered with!");
        //            }
        //            initializeFlowUp();
        //            Rollbar.init(this, "c8c8b4cb1d4f4650a77ae1558865ca87", BuildConfig.DEBUG ? "debug" : "production");
        //        }).subscribeOn(Schedulers.io())
        //                   .subscribe(() -> {
        //                   }, Throwable::printStackTrace);
        initializeRealm()
        DataServiceFactory.init(DataServiceConfig.Builder(this)
                .baseUrl(apiBaseUrl)
                .okHttpBuilder(okHttpBuilder)
                .withCache(3, TimeUnit.MINUTES)
                .withRealm()
                .build())
    }

    private fun initializeRealm() {
        Realm.init(this)
        Realm.setDefaultConfiguration(RealmConfiguration.Builder().name("app.realm")
                .modules(Realm.getDefaultModule(), LibraryModule()).rxFactory(RealmObservableFactory())
                .deleteRealmIfMigrationNeeded().build())
    }

    private fun initializeStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build())
            StrictMode.setVmPolicy(StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build())
        }
    }

    companion object {
        private val TIME_OUT = 15
    }
}
