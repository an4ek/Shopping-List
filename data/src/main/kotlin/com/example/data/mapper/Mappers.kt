package com.example.data.mapper

import com.example.data.db.entity.CategoryEntity
import com.example.data.db.entity.ShoppingItemEntity
import com.example.data.db.entity.ShoppingListEntity
import com.example.domain.model.Category
import com.example.domain.model.ShoppingItem
import com.example.domain.model.ShoppingList

fun ShoppingListEntity.toDomain() = ShoppingList(
    id = id, name = name, isCompleted = isCompleted, createdAt = createdAt
)
fun ShoppingList.toEntity() = ShoppingListEntity(
    id = id, name = name, isCompleted = isCompleted, createdAt = createdAt
)
fun ShoppingItemEntity.toDomain() = ShoppingItem(
    id = id, listId = listId, name = name,
    quantity = quantity, categoryId = categoryId, isChecked = isChecked
)
fun ShoppingItem.toEntity() = ShoppingItemEntity(
    id = id, listId = listId, name = name,
    quantity = quantity, categoryId = categoryId, isChecked = isChecked
)
fun CategoryEntity.toDomain() = Category(id = id, name = name, colorHex = colorHex)
fun Category.toEntity() = CategoryEntity(id = id, name = name, colorHex = colorHex)
