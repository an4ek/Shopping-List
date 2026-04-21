package com.example.shoppinglistapp.di

import com.example.shoppinglistapp.analytics.AnalyticsService
import com.example.shoppinglistapp.analytics.AppMetricaAnalyticsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    @Singleton
    fun provideAnalyticsService(): AnalyticsService = AppMetricaAnalyticsService()
}
