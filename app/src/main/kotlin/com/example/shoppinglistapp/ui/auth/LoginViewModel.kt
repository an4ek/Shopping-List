package com.example.shoppinglistapp.ui.auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoppinglistapp.analytics.AnalyticsService
import com.example.shoppinglistapp.auth.AuthResult
import com.example.shoppinglistapp.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val userName: String? = null,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService,
    private val analytics: AnalyticsService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun loginWithVk(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            when (val result = authService.loginWithVk(activity)) {
                is AuthResult.Success -> {
                    analytics.trackEvent("user_logged_in", mapOf("provider" to "vk"))
                    _uiState.value = LoginUiState(isSuccess = true, userName = result.user.name)
                }
                is AuthResult.Error -> _uiState.value = LoginUiState(error = result.message)
                is AuthResult.Cancelled -> _uiState.value = LoginUiState()
            }
        }
    }

    fun loginWithYandex(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            when (val result = authService.loginWithYandex(activity)) {
                is AuthResult.Success -> {
                    analytics.trackEvent("user_logged_in", mapOf("provider" to "yandex"))
                    _uiState.value = LoginUiState(isSuccess = true, userName = result.user.name)
                }
                is AuthResult.Error -> _uiState.value = LoginUiState(error = result.message)
                is AuthResult.Cancelled -> _uiState.value = LoginUiState()
            }
        }
    }

    fun isLoggedIn(): Boolean = authService.isLoggedIn()

    fun logout() {
        authService.logout()
        _uiState.value = LoginUiState()
    }
}
