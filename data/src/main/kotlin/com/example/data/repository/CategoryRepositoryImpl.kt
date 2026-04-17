package com.example.data.repository

import com.example.data.db.dao.CategoryDao
import com.example.data.mapper.toDomain
import com.example.data.mapper.toEntity
import com.example.domain.model.Category
import com.example.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    private val dao: CategoryDao
) : ICategoryRepository {
    override fun observeAll(): Flow<List<Category>> =
        dao.observeAll().map { it.map { e -> e.toDomain() } }
    override suspend fun create(category: Category) = dao.insert(category.toEntity())
    override suspend fun delete(id: Long) = dao.deleteById(id)
}
