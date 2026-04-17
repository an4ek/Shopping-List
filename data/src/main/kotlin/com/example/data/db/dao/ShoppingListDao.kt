package com.example.data.db.dao

import androidx.room.*
import com.example.data.db.entity.ShoppingListEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_lists ORDER BY created_at DESC")
    fun observeAll(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shopping_lists WHERE is_completed = 1 ORDER BY created_at DESC")
    fun observeCompleted(): Flow<List<ShoppingListEntity>>

    @Query("SELECT * FROM shopping_lists WHERE id = :id")
    suspend fun getById(id: Long): ShoppingListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ShoppingListEntity): Long

    @Update
    suspend fun update(entity: ShoppingListEntity)

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    suspend fun deleteById(id: Long)
}
