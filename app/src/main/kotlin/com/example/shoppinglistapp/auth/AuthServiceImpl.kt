package com.example.shoppinglistapp.auth

import android.app.Activity
import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.vk.api.sdk.VK
import com.vk.api.sdk.auth.VKAuthenticationResult
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthResult
import com.yandex.authsdk.YandexAuthSdk
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthServiceImpl(
    private val tokenRepository: TokenRepository,
    private val context: Context
) : AuthService {

    private var yandexLauncher: ActivityResultLauncher<YandexAuthLoginOptions>? = null
    private var yandexCallback: ((AuthResult) -> Unit)? = null

    fun registerYandexLauncher(activity: ComponentActivity) {
        val sdk = YandexAuthSdk.create(YandexAuthOptions(activity))
        yandexLauncher = activity.registerForActivityResult(sdk.contract) { result ->
            val authResult = when (result) {
                is YandexAuthResult.Success -> {
                    val user = User(
                        id = result.token.value.take(10),
                        name = "Yandex User",
                        email = null,
                        provider = AuthProvider.YANDEX
                    )
                    tokenRepository.saveUser(user)
                    AuthResult.Success(user)
                }
                is YandexAuthResult.Failure -> AuthResult.Error(result.exception.message ?: "Yandex auth failed")
                YandexAuthResult.Cancelled -> AuthResult.Cancelled
            }
            yandexCallback?.invoke(authResult)
            yandexCallback = null
        }
    }

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
                        continuation.resume(AuthResult.Error(result.exception.message ?: "VK auth failed"))
                    }
                }
            }
        }

    override suspend fun loginWithYandex(activity: Activity): AuthResult =
        suspendCoroutine { continuation ->
            val launcher = yandexLauncher
            if (launcher == null) {
                continuation.resume(AuthResult.Error("Yandex launcher not registered"))
                return@suspendCoroutine
            }
            yandexCallback = { result -> continuation.resume(result) }
            launcher.launch(YandexAuthLoginOptions())
        }

    override fun logout() {
        VK.logout()
        tokenRepository.clear()
    }

    override fun getCurrentUser(): User? = tokenRepository.getUser()
    override fun isLoggedIn(): Boolean = tokenRepository.getUser() != null
}
