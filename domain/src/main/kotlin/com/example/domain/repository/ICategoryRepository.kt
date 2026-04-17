package com.example.domain.repository

import com.example.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {
    fun observeAll(): Flow<List<Category>>
    suspend fun create(category: Category): Long
    suspend fun delete(id: Long)
}
