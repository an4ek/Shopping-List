package com.example.domain.repository

import com.example.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface IShoppingItemRepository {
    fun observeItemsByListId(listId: Long): Flow<List<ShoppingItem>>
    suspend fun getItemById(id: Long): ShoppingItem?
    suspend fun addItem(item: ShoppingItem): Long
    suspend fun updateItem(item: ShoppingItem)
    suspend fun deleteItem(id: Long)
    suspend fun checkItem(id: Long, isChecked: Boolean)
    suspend fun deleteAllItemsInList(listId: Long)
}
