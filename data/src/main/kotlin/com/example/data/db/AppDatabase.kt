package com.example.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.data.db.dao.CategoryDao
import com.example.data.db.dao.ShoppingItemDao
import com.example.data.db.dao.ShoppingListDao
import com.example.data.db.entity.CategoryEntity
import com.example.data.db.entity.ShoppingItemEntity
import com.example.data.db.entity.ShoppingListEntity

@Database(
    entities = [ShoppingListEntity::class, ShoppingItemEntity::class, CategoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun shoppingListDao(): ShoppingListDao
    abstract fun shoppingItemDao(): ShoppingItemDao
    abstract fun categoryDao(): CategoryDao
}
