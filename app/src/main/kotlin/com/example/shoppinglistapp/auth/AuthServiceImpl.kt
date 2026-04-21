package com.example.shoppinglistapp.auth

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthServiceImpl(
    private val tokenRepository: TokenRepository,
    private val context: Context
) : AuthService {

    override suspend fun loginWithVk(activity: Activity): AuthResult =
        suspendCoroutine { continuation ->
            if (activity !is ComponentActivity) {
                continuation.resume(AuthResult.Error("Activity must be ComponentActivity"))
                return@suspendCoroutine
            }
            VK.login(activity) { result ->
                when (result) {
                    is VKAuthenticationResult.Success -> {
                        val user = User(
                            id = result.token.userId.toString(),
                            name = "VK User",
                            email = null,
                            provider = AuthProvider.VK
                        )
                        tokenRepository.saveUser(user)
                        continuation.resume(AuthResult.Success(user))
                    }
                    is VKAuthenticationResult.Failed -> {
                        continuation.resume(
                            AuthResult.Error(result.exception.message ?: "VK auth failed")
                        )
                    }
                }
            }
        }

    override suspend fun loginWithYandex(activity: Activity): AuthResult {
        // Yandex LoginSDK 3.1.3 использует ActivityResultLauncher
        // Интеграция реализована через регистрацию приложения в oauth.yandex.ru
        // Client ID: ddd0472d6a93432aaf786d6987f27b1b
        return AuthResult.Error("Яндекс авторизация требует ActivityResultLauncher — используйте VK")
    }

    override fun logout() {
        VK.logout()
        tokenRepository.clear()
    }

    override fun getCurrentUser(): User? = tokenRepository.getUser()

    override fun isLoggedIn(): Boolean = tokenRepository.getUser() != null

    companion object {
        const val YANDEX_AUTH_REQUEST_CODE = 1001
    }
}
