package com.example.shoppinglistapp

import com.example.shoppinglistapp.analytics.FakeAnalyticsService
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ShoppingListViewModelTest {

    private lateinit var fakeAnalytics: FakeAnalyticsService

    @Before
    fun setup() {
        fakeAnalytics = FakeAnalyticsService()
    }

    @Test
    fun `when screen_viewed event tracked, name is correct`() {
        fakeAnalytics.trackEvent("screen_viewed", mapOf("screen_name" to "shopping_lists"))
        assertEquals("screen_viewed", fakeAnalytics.lastEventName())
    }

    @Test
    fun `when screen_viewed event tracked, screen_name param is correct`() {
        fakeAnalytics.trackEvent("screen_viewed", mapOf("screen_name" to "shopping_lists"))
        assertEquals("shopping_lists", fakeAnalytics.lastEventParams()?.get("screen_name"))
    }

    @Test
    fun `when list_created event tracked, name is correct`() {
        fakeAnalytics.trackEvent("list_created", mapOf("list_name" to "Test List"))
        assertEquals("list_created", fakeAnalytics.lastEventName())
    }

    @Test
    fun `when multiple events tracked, all are saved`() {
        fakeAnalytics.trackEvent("screen_viewed")
        fakeAnalytics.trackEvent("list_created")
        fakeAnalytics.trackEvent("list_deleted")
        assertEquals(3, fakeAnalytics.trackedEvents.size)
    }
}
