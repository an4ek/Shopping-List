package com.example.domain.usecase.item

import com.example.domain.model.ShoppingItem
import com.example.domain.repository.IShoppingItemRepository
import kotlinx.coroutines.flow.Flow

class GetItemsByListUseCase(
    private val repository: IShoppingItemRepository
) {
    operator fun invoke(listId: Long): Flow<List<ShoppingItem>> =
        repository.observeItemsByListId(listId)
}
