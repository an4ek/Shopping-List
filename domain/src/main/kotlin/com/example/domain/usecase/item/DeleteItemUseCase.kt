package com.example.domain.usecase.item

import com.example.domain.repository.IShoppingItemRepository

class DeleteItemUseCase(
    private val repository: IShoppingItemRepository
) {
    suspend operator fun invoke(itemId: Long) {
        repository.deleteItem(itemId)
    }
}
