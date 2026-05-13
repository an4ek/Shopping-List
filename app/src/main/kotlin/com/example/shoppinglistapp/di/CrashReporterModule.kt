package com.example.shoppinglistapp.di

import com.example.shoppinglistapp.crash.AppMetricaCrashReporter
import com.example.shoppinglistapp.crash.CompositeCrashReporter
import com.example.shoppinglistapp.crash.CrashReporter
import com.example.shoppinglistapp.crash.FirebaseCrashReporter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CrashReporterModule {

    @Provides
    @Singleton
    fun provideCrashReporter(): CrashReporter =
        CompositeCrashReporter(
            listOf(
                FirebaseCrashReporter(),
                AppMetricaCrashReporter()
            )
        )
}
