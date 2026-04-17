package com.example.core.navigation

object AppRoutes {
    const val SHOPPING_LISTS = "shopping_lists"
    const val SHOPPING_ITEMS = "shopping_items/{listId}"

    fun shoppingItems(listId: Long) = "shopping_items/$listId"
}
