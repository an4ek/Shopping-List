package com.example.shoppinglistapp

import android.app.Application
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.shoppinglistapp.worker.SyncWorker
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.vk.api.sdk.VK
import dagger.hilt.android.HiltAndroidApp
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class ShoppingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initAppMetrica()
        initVk()
        initCrashlytics()
        initWorkManager()
    }

    private fun initAppMetrica() {
        val config = AppMetricaConfig
            .newConfigBuilder(BuildConfig.APPMETRICA_API_KEY)
            .withLogs()
            .withCrashReporting(true)
            .withSessionTimeout(60)
            .build()
        AppMetrica.activate(applicationContext, config)
        AppMetrica.enableActivityAutoTracking(this)
    }

    private fun initVk() {
        VK.initialize(this)
    }

    private fun initCrashlytics() {
        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(true)
    }

    private fun initWorkManager() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            SyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }
}
