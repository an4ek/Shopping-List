package com.example.shoppinglistapp.analytics

import android.util.Log

class FakeAnalyticsService : AnalyticsService {
    val trackedEvents = mutableListOf<Pair<String, Map<String, Any>>>()

    override fun trackEvent(name: String, params: Map<String, Any>) {
        trackedEvents.add(Pair(name, params))
        Log.d("FakeAnalytics", "trackEvent: $name, params: $params")
    }

    override fun trackError(message: String, error: Throwable?) {
        Log.d("FakeAnalytics", "trackError: $message, error: ${error?.message}")
    }

    fun lastEventName(): String? = trackedEvents.lastOrNull()?.first

    fun lastEventParams(): Map<String, Any>? = trackedEvents.lastOrNull()?.second

    fun clear() = trackedEvents.clear()
}
