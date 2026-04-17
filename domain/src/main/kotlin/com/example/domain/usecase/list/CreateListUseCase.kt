package com.example.domain.usecase.list

import com.example.domain.model.ShoppingList
import com.example.domain.repository.IShoppingListRepository

class CreateListUseCase(
    private val repository: IShoppingListRepository
) {
    suspend operator fun invoke(name: String): Long {
        if (name.isBlank()) throw IllegalArgumentException("List name cannot be empty")
        if (name.length > 100) throw IllegalArgumentException("List name is too long")
        return repository.createList(ShoppingList(name = name.trim()))
    }
}
