package com.example.domain.usecase.list

import com.example.domain.repository.IShoppingListRepository

class CompleteListUseCase(
    private val repository: IShoppingListRepository
) {
    suspend operator fun invoke(listId: Long) {
        val list = repository.getListById(listId)
            ?: throw IllegalArgumentException("List not found")
        repository.updateList(list.copy(isCompleted = true))
    }
}
