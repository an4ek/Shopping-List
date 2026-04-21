package com.example.shoppinglistapp.auth

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class TokenRepository(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_auth_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveUser(user: User) {
        prefs.edit()
            .putString("user_id", user.id)
            .putString("user_name", user.name)
            .putString("user_email", user.email)
            .putString("user_provider", user.provider.name)
            .apply()
    }

    fun getUser(): User? {
        val id = prefs.getString("user_id", null) ?: return null
        val name = prefs.getString("user_name", null) ?: return null
        val provider = prefs.getString("user_provider", null) ?: return null
        return User(
            id = id,
            name = name,
            email = prefs.getString("user_email", null),
            provider = AuthProvider.valueOf(provider)
        )
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
