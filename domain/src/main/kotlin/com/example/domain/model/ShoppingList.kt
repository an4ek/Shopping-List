package com.example.domain.model

data class ShoppingList(
    val id: Long = 0,
    val name: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
