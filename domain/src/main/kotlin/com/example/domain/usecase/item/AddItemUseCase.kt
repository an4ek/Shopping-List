package com.example.domain.usecase.item

import com.example.domain.model.ShoppingItem
import com.example.domain.repository.IShoppingItemRepository

class AddItemUseCase(
    private val repository: IShoppingItemRepository
) {
    suspend operator fun invoke(listId: Long, name: String, quantity: String = "1", categoryId: Long? = null): Long {
        if (name.isBlank()) throw IllegalArgumentException("Item name cannot be empty")
        val item = ShoppingItem(
            listId = listId,
            name = name.trim(),
            quantity = quantity.trim().ifBlank { "1" },
            categoryId = categoryId
        )
        return repository.addItem(item)
    }
}
