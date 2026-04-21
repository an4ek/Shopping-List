package com.example.shoppinglistapp.auth

data class User(
    val id: String,
    val name: String,
    val email: String?,
    val provider: AuthProvider
)

enum class AuthProvider { VK, YANDEX }

sealed class AuthResult {
    data class Success(val user: User) : AuthResult()
    data class Error(val message: String) : AuthResult()
    object Cancelled : AuthResult()
}
