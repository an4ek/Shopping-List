package com.example.shoppinglistapp.auth

import android.app.Activity

interface AuthService {
    suspend fun loginWithVk(activity: Activity): AuthResult
    suspend fun loginWithYandex(activity: Activity): AuthResult
    fun logout()
    fun isLoggedIn(): Boolean
    fun getCurrentUser(): User?
}
