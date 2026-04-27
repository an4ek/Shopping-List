package com.example.shoppinglistapp.config

interface RemoteConfigService {
    suspend fun fetchAndActivate()
    fun getWelcomeBannerText(): String
    fun isPromoBannerEnabled(): Boolean
}
