package com.example.domain.model

data class ShoppingItem(
    val id: Long = 0,
    val listId: Long,
    val name: String,
    val quantity: String = "1",
    val categoryId: Long? = null,
    val isChecked: Boolean = false
)
