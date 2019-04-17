package com.zeyad.rxredux.di

import android.content.Context
import android.util.Log
import com.zeyad.rxredux.BuildConfig
import com.zeyad.rxredux.screens.detail.UserDetailVM
import com.zeyad.rxredux.screens.list.UserListVM
import com.zeyad.rxredux.utils.Constants.URLS.API_BASE_URL
import com.zeyad.usecases.api.DataServiceConfig
import com.zeyad.usecases.api.DataServiceFactory
import com.zeyad.usecases.api.IDataService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.module
import java.util.concurrent.TimeUnit

val myModule: Module = module {
    viewModel { UserListVM(get()) }
    viewModel { UserDetailVM(get()) }

    single { createDataService(get()) }
}

fun createDataService(context: Context): IDataService {
    DataServiceFactory(DataServiceConfig.Builder(context)
            .baseUrl(API_BASE_URL)
            .okHttpBuilder(getOkHttpBuilder())
//            .withRealm()
            .build())
    return DataServiceFactory.dataService!!
}

fun getOkHttpBuilder(): OkHttpClient.Builder {
    return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor { Log.d("NetworkInfo", it) }
                    .setLevel(if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
                    else HttpLoggingInterceptor.Level.NONE))
            .connectTimeout(15L, TimeUnit.SECONDS)
            .writeTimeout(15L, TimeUnit.SECONDS)
            .readTimeout(15L, TimeUnit.SECONDS)
}
