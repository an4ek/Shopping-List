package com.example.data.repository

import com.example.data.db.dao.ShoppingListDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.ShoppingList
import com.example.domain.repository.IShoppingListRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ShoppingListRepositoryImpl @Inject constructor(
    private val dao: ShoppingListDao
) : IShoppingListRepository {
    override fun observeAllLists(): Flow<List<ShoppingList>> =
        dao.observeAll().map { it.map { e -> e.toDomain() } }
    override fun observeCompletedLists(): Flow<List<ShoppingList>> =
        dao.observeCompleted().map { it.map { e -> e.toDomain() } }
    override suspend fun getListById(id: Long) = dao.getById(id)?.toDomain()
    override suspend fun createList(list: ShoppingList) = dao.insert(list.toEntity())
    override suspend fun updateList(list: ShoppingList) = dao.update(list.toEntity())
    override suspend fun deleteList(id: Long) = dao.deleteById(id)
}
