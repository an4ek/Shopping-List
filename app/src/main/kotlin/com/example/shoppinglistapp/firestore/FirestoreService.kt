package com.example.shoppinglistapp.firestore

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val fcmToken: String = "",
    val updatedAt: Long = 0L
)

@Singleton
class FirestoreService @Inject constructor() {

    private val db = FirebaseFirestore.getInstance()

    suspend fun saveUserProfile(profile: UserProfile) {
        try {
            db.collection("users")
                .document(profile.userId)
                .set(profile)
                .await()
            Log.d(TAG, "User profile saved: ${profile.userId}")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving user profile", e)
        }
    }

    suspend fun updateFcmToken(userId: String, token: String) {
        try {
            db.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "fcmToken" to token,
                        "updatedAt" to System.currentTimeMillis()
                    )
                )
                .await()
            Log.d(TAG, "FCM token updated for user: $userId")
        } catch (e: Exception) {
            Log.e(TAG, "Error updating FCM token", e)
        }
    }

    fun observeUserProfile(userId: String): Flow<UserProfile?> = callbackFlow {
        var registration: ListenerRegistration? = null
        try {
            registration = db.collection("users")
                .document(userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Error observing user profile", error)
                        return@addSnapshotListener
                    }
                    val profile = snapshot?.toObject(UserProfile::class.java)
                    trySend(profile)
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error setting up snapshot listener", e)
        }
        awaitClose { registration?.remove() }
    }

    companion object {
        private const val TAG = "FirestoreService"
    }
}
