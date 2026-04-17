package com.example.domain.usecase.category

import com.example.domain.model.Category
import com.example.domain.repository.ICategoryRepository

class CreateCategoryUseCase(
    private val repository: ICategoryRepository
) {
    suspend operator fun invoke(name: String, colorHex: String = "#4CAF50"): Long {
        if (name.isBlank()) throw IllegalArgumentException("Category name cannot be empty")
        return repository.create(Category(name = name.trim(), colorHex = colorHex))
    }
}
