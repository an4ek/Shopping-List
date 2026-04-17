package com.example.data.db.dao

import androidx.room.*
import com.example.data.db.entity.ShoppingItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items WHERE list_id = :listId ORDER BY is_checked ASC, id ASC")
    fun observeByListId(listId: Long): Flow<List<ShoppingItemEntity>>

    @Query("SELECT * FROM shopping_items WHERE id = :id")
    suspend fun getById(id: Long): ShoppingItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: ShoppingItemEntity): Long

    @Update
    suspend fun update(entity: ShoppingItemEntity)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE shopping_items SET is_checked = :isChecked WHERE id = :id")
    suspend fun checkItem(id: Long, isChecked: Boolean)

    @Query("DELETE FROM shopping_items WHERE list_id = :listId")
    suspend fun deleteByListId(listId: Long)
}
