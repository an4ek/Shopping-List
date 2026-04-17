package com.example.domain.usecase.category

import com.example.domain.model.Category
import com.example.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow

class GetAllCategoriesUseCase(
    private val repository: ICategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.observeAll()
}
