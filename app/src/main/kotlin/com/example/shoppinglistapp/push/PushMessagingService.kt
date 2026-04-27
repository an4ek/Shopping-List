package com.example.shoppinglistapp.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.shoppinglistapp.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PushMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM Token: $token")
        saveToken(token)
        // Обновляем токен в Firestore
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                val userId = prefs.getString(KEY_USER_ID, null)
                if (userId != null) {
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    db.collection("users").document(userId)
                        .update("fcmToken", token, "updatedAt", System.currentTimeMillis())
                    Log.d(TAG, "FCM token updated in Firestore for user: $userId")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error updating token in Firestore", e)
            }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        Log.d(TAG, "Message from: ${message.from}")
        Log.d(TAG, "Data: ${message.data}")
        message.notification?.let {
            Log.d(TAG, "Notification title: ${it.title}, body: ${it.body}")
        }
        val title = message.notification?.title ?: message.data["title"] ?: "Список покупок"
        val body = message.notification?.body ?: message.data["body"] ?: "Новое уведомление"
        val screen = message.data["screen"] ?: "lists"
        showNotification(title, body, screen)
    }

    private fun saveToken(token: String) {
        val prefs: SharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        Log.d(TAG, "Token saved to SharedPreferences")
    }

    private fun showNotification(title: String, body: String, screen: String) {
        createNotificationChannel()
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("screen", screen)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Push уведомления",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Уведомления приложения Список покупок"
        }
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val TAG = "PushMessagingService"
        private const val CHANNEL_ID = "shopping_list_channel"
        const val PREFS_NAME = "push_prefs"
        const val KEY_FCM_TOKEN = "fcm_token"
        const val KEY_USER_ID = "user_id"
    }
}
