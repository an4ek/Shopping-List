package com.example.shoppinglistapp

import android.app.Application
import com.vk.api.sdk.VK
import dagger.hilt.android.HiltAndroidApp
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig

@HiltAndroidApp
class ShoppingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initAppMetrica()
        initVk()
    }

    private fun initAppMetrica() {
        val config = AppMetricaConfig
            .newConfigBuilder(BuildConfig.APPMETRICA_API_KEY)
            .withLogs()
            .withSessionTimeout(60)
            .build()
        AppMetrica.activate(applicationContext, config)
        AppMetrica.enableActivityAutoTracking(this)
    }

    private fun initVk() {
        VK.initialize(this)
    }
}
