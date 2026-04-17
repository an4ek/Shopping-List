package com.example.domain.usecase.list

import com.example.domain.repository.IShoppingItemRepository
import com.example.domain.repository.IShoppingListRepository

class DeleteListUseCase(
    private val listRepository: IShoppingListRepository,
    private val itemRepository: IShoppingItemRepository
) {
    suspend operator fun invoke(listId: Long) {
        itemRepository.deleteAllItemsInList(listId)
        listRepository.deleteList(listId)
    }
}
