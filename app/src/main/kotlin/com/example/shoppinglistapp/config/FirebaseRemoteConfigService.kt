package com.example.shoppinglistapp.config

import android.util.Log
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseRemoteConfigService @Inject constructor() : RemoteConfigService {

    private val remoteConfig = FirebaseRemoteConfig.getInstance().apply {
        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(0) // 0 для тестирования
            .build()
        setConfigSettingsAsync(settings)
        setDefaultsAsync(mapOf(
            KEY_WELCOME_BANNER_TEXT to "Добро пожаловать в Список покупок!",
            KEY_SHOW_PROMO_BANNER to false
        ))
    }

    override suspend fun fetchAndActivate() {
        try {
            val activated = remoteConfig.fetchAndActivate().await()
            Log.d(TAG, "Remote Config activated: $activated")
        } catch (e: Exception) {
            Log.e(TAG, "Remote Config fetch failed", e)
        }
    }

    override fun getWelcomeBannerText(): String =
        remoteConfig.getString(KEY_WELCOME_BANNER_TEXT)

    override fun isPromoBannerEnabled(): Boolean =
        remoteConfig.getBoolean(KEY_SHOW_PROMO_BANNER)

    companion object {
        private const val TAG = "RemoteConfigService"
        private const val KEY_WELCOME_BANNER_TEXT = "welcome_banner_text"
        private const val KEY_SHOW_PROMO_BANNER = "show_promo_banner"
    }
}
