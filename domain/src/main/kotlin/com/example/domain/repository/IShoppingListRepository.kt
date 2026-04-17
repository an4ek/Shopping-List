package com.example.domain.repository

import com.example.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface IShoppingListRepository {
    fun observeAllLists(): Flow<List<ShoppingList>>
    suspend fun getListById(id: Long): ShoppingList?
    suspend fun createList(list: ShoppingList): Long
    suspend fun updateList(list: ShoppingList)
    suspend fun deleteList(id: Long)
    fun observeCompletedLists(): Flow<List<ShoppingList>>
}
