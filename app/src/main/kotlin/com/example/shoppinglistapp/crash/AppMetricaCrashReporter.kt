package com.example.shoppinglistapp.crash

import android.util.Log
import io.appmetrica.analytics.AppMetrica
import javax.inject.Inject

class AppMetricaCrashReporter @Inject constructor() : CrashReporter {

    override fun log(message: String) {
        AppMetrica.reportEvent(message)
        Log.d(TAG, "CrashReporter log: $message")
    }

    override fun setKey(key: String, value: String) {
        Log.d(TAG, "CrashReporter setKey: $key = $value")
    }

    override fun setUserId(userId: String?) {
        Log.d(TAG, "CrashReporter setUserId: $userId")
    }

    override fun recordNonFatal(throwable: Throwable) {
        AppMetrica.reportError("non_fatal_error", throwable.message ?: "Unknown error", throwable)
        Log.e(TAG, "CrashReporter recordNonFatal", throwable)
    }

    companion object {
        private const val TAG = "AppMetricaCrashReporter"
    }
}
