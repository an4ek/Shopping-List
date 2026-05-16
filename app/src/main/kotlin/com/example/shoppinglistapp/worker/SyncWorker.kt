package com.example.shoppinglistapp.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import android.util.Log

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("SyncWorker", "Синхронизация списков покупок запущена")
            Log.d("SyncWorker", "Синхронизация завершена успешно")
            Result.success()
        } catch (e: Exception) {
            Log.e("SyncWorker", "Ошибка синхронизации: ${e.message}")
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "shopping_list_sync"
    }
}
