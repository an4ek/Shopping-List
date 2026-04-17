package com.example.core.navigation

object AppRoutes {
    const val LISTS = "lists"
    const val ITEMS = "items/{listId}"
    fun items(listId: Long) = "items/$listId"
}
