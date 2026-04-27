package com.example.shoppinglistapp.di

import com.example.shoppinglistapp.config.FirebaseRemoteConfigService
import com.example.shoppinglistapp.config.RemoteConfigService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteConfigModule {

    @Provides
    @Singleton
    fun provideRemoteConfigService(): RemoteConfigService =
        FirebaseRemoteConfigService()
}
