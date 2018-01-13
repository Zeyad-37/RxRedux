package com.zeyad.rxredux;

import android.app.Application;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;

import com.zeyad.rxredux.core.eventbus.RxEventBusFactory;
import com.zeyad.usecases.api.DataServiceConfig;
import com.zeyad.usecases.api.DataServiceFactory;
import com.zeyad.usecases.network.ProgressInterceptor;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;
import okhttp3.CertificatePinner;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

import static com.zeyad.rxredux.utils.Constants.URLS.API_BASE_URL;


/**
 * @author by Zeyad on 7/27/17.
 */
public class RxReduxApplication extends Application {
    private static final int TIME_OUT = 15;

    @Override
    public void onCreate() {
        super.onCreate();
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
        initializeRealm();
        DataServiceFactory.init(new DataServiceConfig.Builder(this)
                .baseUrl(getApiBaseUrl())
                .okHttpBuilder(getOkHttpBuilder())
                .withCache(3, TimeUnit.MINUTES)
                .withRealm()
                .build());
    }

    @NonNull
    OkHttpClient.Builder getOkHttpBuilder() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new ProgressInterceptor(
                (bytesRead, contentLength, done) -> RxEventBusFactory.getInstance().send(null)) {
            @Override
            public boolean isFileIO(Response originalResponse) {
                return false;
            }
        }).addInterceptor(new HttpLoggingInterceptor(message -> Log.d("NetworkInfo", message)).setLevel(
                BuildConfig.DEBUG ? HttpLoggingInterceptor.Level.BODY : HttpLoggingInterceptor.Level.NONE))
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS).readTimeout(TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                .certificatePinner(new CertificatePinner.Builder()
                        .add(API_BASE_URL, "sha256/6wJsqVDF8K19zxfLxV5DGRneLyzso9adVdUN/exDacw")
                        .add(API_BASE_URL, "sha256/k2v657xBsOVe1PQRwOsHsw3bsGT2VzIqz5K+59sNQws=")
                        .add(API_BASE_URL, "sha256/WoiWRyIOVNa9ihaBciRSC7XHjliYS9VwUGOIud4PB18=").build())
                .connectionSpecs(Arrays.asList(ConnectionSpec.MODERN_TLS, ConnectionSpec.COMPATIBLE_TLS));
        //        if (getSSlSocketFactory() != null && getX509TrustManager() != null) {
        //            builder.sslSocketFactory(getSSlSocketFactory(), getX509TrustManager());
        //        }
        return builder;
    }

    @NonNull
    String getApiBaseUrl() {
        return API_BASE_URL;
    }

    private void initializeRealm() {
        Realm.init(this);
        Realm.setDefaultConfiguration(new RealmConfiguration.Builder().name("app.realm")
                .modules(Realm.getDefaultModule(), new LibraryModule()).rxFactory(new RealmObservableFactory())
                .deleteRealmIfMigrationNeeded().build());
    }

    private void initializeStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
    }
}
