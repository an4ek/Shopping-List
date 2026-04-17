package com.example.data.repository

import com.example.data.db.dao.ShoppingItemDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.ShoppingItem
import com.example.domain.repository.IShoppingItemRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingItemRepositoryImpl @Inject constructor(
    private val dao: ShoppingItemDao
) : IShoppingItemRepository {
    override fun observeItemsByListId(listId: Long): Flow<List<ShoppingItem>> =
        dao.observeByListId(listId).map { it.map { e -> e.toDomain() } }
    override suspend fun getItemById(id: Long) = dao.getById(id)?.toDomain()
    override suspend fun addItem(item: ShoppingItem) = dao.insert(item.toEntity())
    override suspend fun updateItem(item: ShoppingItem) = dao.update(item.toEntity())
    override suspend fun deleteItem(id: Long) = dao.deleteById(id)
    override suspend fun checkItem(id: Long, isChecked: Boolean) = dao.checkItem(id, isChecked)
    override suspend fun deleteAllItemsInList(listId: Long) = dao.deleteByListId(listId)
}
