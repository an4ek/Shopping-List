package com.example.core.navigation

object AppRoutes {
    const val SHOPPING_LISTS = "shopping_lists"
    const val SHOPPING_ITEMS = "shopping_items/{listId}"
    const val CATEGORIES = "categories"
    const val HISTORY = "history"

    fun shoppingItems(listId: Long) = "shopping_items/$listId"
}
