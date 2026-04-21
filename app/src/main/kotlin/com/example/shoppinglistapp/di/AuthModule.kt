package com.example.shoppinglistapp.di

import android.content.Context
import com.example.shoppinglistapp.auth.AuthService
import com.example.shoppinglistapp.auth.AuthServiceImpl
import com.example.shoppinglistapp.auth.TokenRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideTokenRepository(@ApplicationContext context: Context): TokenRepository =
        TokenRepository(context)

    @Provides
    @Singleton
    fun provideAuthService(
        tokenRepository: TokenRepository,
        @ApplicationContext context: Context
    ): AuthService = AuthServiceImpl(tokenRepository, context)
}
