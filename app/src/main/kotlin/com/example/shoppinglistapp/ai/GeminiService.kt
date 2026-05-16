package com.example.shoppinglistapp.ai

import android.util.Log
import com.example.shoppinglistapp.BuildConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun suggestItems(prompt: String): List<String> {
        return try {
            val json = JSONObject().apply {
                put("model", "google/gemini-2.0-flash-exp:free")
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", "Составь список продуктов для: $prompt. Верни только список, каждый пункт с новой строки, без нумерации и лишнего текста. Максимум 10 пунктов.")
                    })
                })
            }

            val request = Request.Builder()
                .url("https://openrouter.ai/api/v1/chat/completions")
                .addHeader("Authorization", "Bearer ${BuildConfig.GEMINI_API_KEY}")
                .addHeader("Content-Type", "application/json")
                .post(json.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: return emptyList()
            val content = JSONObject(body)
                .getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")

            content.lines()
                .map { it.trim() }
                .filter { it.isNotBlank() }
        } catch (e: Exception) {
            Log.e("GeminiService", "Ошибка: ${e.message}")
            emptyList()
        }
    }
}
