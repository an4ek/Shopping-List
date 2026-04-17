package com.example.domain.usecase.list

import com.example.domain.model.ShoppingList
import com.example.domain.repository.IShoppingListRepository
import kotlinx.coroutines.flow.Flow

class GetAllListsUseCase(
    private val repository: IShoppingListRepository
) {
    operator fun invoke(): Flow<List<ShoppingList>> =
        repository.observeAllLists()
}
