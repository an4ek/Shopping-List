package com.example.domain.usecase.item

import com.example.domain.repository.IShoppingItemRepository

class CheckItemUseCase(
    private val repository: IShoppingItemRepository
) {
    suspend operator fun invoke(itemId: Long, isChecked: Boolean) {
        repository.checkItem(itemId, isChecked)
    }
}
