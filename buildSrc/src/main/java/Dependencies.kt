
object Versions {
    // Gradle
    const val android_gradle_plugin = "3.0.1"

    // Kotlin
    val kotlin = "1.3.11"

    // Realm
    val realm = "4.2.0"

    // SDK
    val min_sdk = 21
    val target_sdk = 28
    val compile_sdk = 28
    val buildTool = "28.0.3"

    // App Version
    val version_code = 1
    val version_name = "1.0"

    // Libs
    // - Support
    val support_lib = "28.0.0"
    val contraint_layout = "1.1.3"
    val archComp = "1.1.1"

    // - Network
    val retrofit = "2.5.0"
    val glide = "3.8.0"
    val okhttpVersion = "3.12.1"

    // - Reactive
    val rxjava = "2.2.0"
    val rxAndroid = "2.1.0"
    val rxbinding = "2.1.1"
    val rxredux = "2.1.2"

    // - Injection
    val koin_version = "1.0.2"

    // - Tools
    val lottie = "2.2.0"
    val rxlint = "1.6"
    val leakCanary = "1.5.4"

    // - Testing
    val androidSupportTest = "1.0.1"
    val espressoCore = "3.0.2"
    val powerMock = "1.7.3"
    val robolectric = "3.5.1"
    val okhttpIdelingResource = "1.0.0"
    val mockito = "2.10.0"
    val mockitoKotlin = "1.5.0"
    val restMock = "0.2.2"
    val junit = "4.12"

    val genericRecyclerViewAdapter = "1.9.1"
    val appId = "com.remotegen.app"
}

object Deps {
    // Gradle
    val android_gradle_plugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    val infer = "com.uber:infer-plugin:0.7.4"
    val error_prone = "net.ltgt.gradle:gradle-errorprone-plugin:0.0.13"
    val pitest = "pl.droidsonroids.gradle:gradle-pitest-plugin:0.1.4"
    val jacoco = "com.dicedmelon.gradle:jacoco-android:0.1.2"
    val kotlinter = "gradle.plugin.org.jmailen.gradle:kotlinter-gradle:1.5.1"
    val detekt = "gradle.plugin.io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.0.0-rework-beta5"
    val dokka = "org.jetbrains.dokka:dokka-android-gradle-plugin:0.9.15"

    val google_play_services = "com.google.android.gms:play-services-location:15.0.0"

    // Kotlin
    val kotlin_stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    val kotlin_gradle_plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jre7:${Versions.kotlin}"
    val kotlin_all_open = "org.jetbrains.kotlin:kotlin-allopen:${Versions.kotlin}"

    // Libs
    val realm = "io.realm:realm-gradle-plugin:${Versions.realm}"
    val use_cases = "com.github.Zeyad-37:Usecases:2.0.1"

    // - Support
    val design_support = "com.android.support:design:${Versions.support_lib}"
    val appcompat_v7 = "com.android.support:appcompat-v7:${Versions.support_lib}"
    val support_v4 = "com.android.support:support-v4:${Versions.support_lib}"
    val recycler_view = "com.android.support:recyclerview-v7:${Versions.support_lib}"
    val card_view = "com.android.support:cardview-v7:${Versions.support_lib}"
    val palette = "com.android.support:palette-v7:${Versions.support_lib}"
    val constraint_layout = "com.android.support.constraint:constraint-layout:${Versions
            .contraint_layout}"
    val arch_life_cycle = "android.arch.lifecycle:extensions:${Versions.archComp}"
    val arch_reactive_stream = "android.arch.lifecycle:reactivestreams:${Versions.archComp}"
    val arch_rxjava_paging = "android.arch.paging:rxjava2:1.0.0-alpha1"

    // - Network
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofit_rxjava = "com.squareup.retrofit2:adapter-rxjava2:${Versions.retrofit}"
    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val okhttp = "3.9.1"
    val reactive_netork = "com.github.pwittchen:reactivenetwork-rx2:0.12.2"

    // - Reactive
    val rx_android = "io.reactivex.rxjava2:rxandroid:${Versions.rxAndroid}"
    val rx_java = "io.reactivex.rxjava2:rxjava:${Versions.rxjava}"
    val rx_binding_core = "com.jakewharton.rxbinding2:rxbinding-kotlin:${Versions.rxbinding}"
    val rx_binding_app_compat = "com.jakewharton.rxbinding2:rxbinding-appcompat-v7-kotlin:${Versions
            .rxbinding}"
    val rx_binding_design = "com.jakewharton.rxbinding2:rxbinding-design-kotlin:${Versions.rxbinding}"
    val rx_binding_recycler_view = "com.jakewharton.rxbinding2:rxbinding-recyclerview-v7-kotlin:${Versions
            .rxbinding}"
    val rx_redux = "com.github.Zeyad-37:RxRedux:${Versions.rxredux}"
    val kotlinx_coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:0.22.5"
    val rx_replay_share = "com.jakewharton.rx2:replaying-share-kotlin:2.0.1"

    // - Injection
    val koin_core = "org.koin:koin-core:${Versions.koin_version}"
    val koin_android = "org.koin:koin-android:${Versions.koin_version}"
    val koin_arch = "org.koin:koin-android-viewmodel:${Versions.koin_version}"

    // - Tools
    val lottie = "com.airbnb.android:lottie:${Versions.lottie}"
    val rxlint = "nl.littlerobots.rxlint:rxlint:${Versions.rxlint}"
    val leakCanary = "com.squareup.leakcanary:leakcanary-android:${Versions.leakCanary}"
    val leakCanary_no_op = "com.squareup.leakcanary:leakcanary-android-no-op:${Versions.leakCanary}"
    val generic_recycler_review = "com.github.Zeyad-37:GenericRecyclerViewAdapter:${Versions.genericRecyclerViewAdapter}"
    val timber = "com.jakewharton.timber:timber:4.7.1"

    // - Testing
    val junit = "junit:junit:${Versions.junit}"
    val support_annotation = "com.android.support:support-annotations:${Versions.support_lib}"
    val mockito = "org.mockito:mockito-core:${Versions.mockito}"
    val mockito_kotlin = "com.nhaarman:mockito-kotlin:${Versions.mockitoKotlin}"
    val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    val robolectric_shadow_support_v4 = "org.robolectric:shadows-support-v4:3.3.2"

    val kotlin_test = "org.jetbrains.kotlin:kotlin-test:${Versions.kotlin}"
    val kotlin_junit = "org.jetbrains.kotlin:kotlin-test-junit:${Versions.kotlin}"
    val kotlin_test_common = "org.jetbrains.kotlin:kotlin-test-common:${Versions.kotlin}"
    val kotlin_annotations_common = "org.jetbrains.kotlin:kotlin-test-annotations-common:${Versions.kotlin}"

    val power_mock_junit = "org.powermock:powermock-module-junit4:${Versions.powerMock}"
    val power_mock_junit_rule = "org.powermock:powermock-module-junit4-rule:${Versions.powerMock}"
    val power_mock_mockito = "org.powermock:powermock-api-mockito:${Versions.powerMock}"
    val power_mock_class_loading_xstream = "org.powermock:powermock-classloading-xstream:${Versions
            .powerMock}"

    val mock_web_server = "com.squareup.okhttp3:mockwebserver:${Versions.okhttpVersion}"
    val rest_mock = "com.github.andrzejchm.RESTMock:android:${Versions.restMock}"

    val okHttp_ideling_resource = "com.jakewharton.espresso:okhttp3-idling-resource:${Versions
            .okhttpIdelingResource}"

    val esspresso_core = "com.android.support.test.espresso:espresso-core:${Versions.espressoCore}"
    val support_test_runner = "com.android.support.test:runner:${Versions.androidSupportTest}"
    val support_test_rules = "com.android.support.test:rules:${Versions.androidSupportTest}"
}

object Plugins {
    val app = "com.android.application"
    val library = "com.android.library"
    val kt_and = "kotlin-android"
    val kt_and_x = "kotlin-android-extensions"
    val kt_all_open = "kotlin-allopen"
    val kapt = "kotlin-kapt"
    val realm = "realm-android"
    val jacoco = "jacoco-android"
    val kotlinter = "org.jmailen.kotlinter"
}