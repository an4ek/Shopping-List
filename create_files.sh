# domain - models
cat > domain/src/main/kotlin/com/example/domain/model/ShoppingList.kt << 'KOTLIN'
package com.example.domain.model

data class ShoppingList(
    val id: Long = 0,
    val name: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/model/ShoppingItem.kt << 'KOTLIN'
package com.example.domain.model

data class ShoppingItem(
    val id: Long = 0,
    val listId: Long,
    val name: String,
    val quantity: String = "1",
    val categoryId: Long? = null,
    val isChecked: Boolean = false
)
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/model/Category.kt << 'KOTLIN'
package com.example.domain.model

data class Category(
    val id: Long = 0,
    val name: String,
    val colorHex: String = "#4CAF50"
)
KOTLIN

# domain - repositories
cat > domain/src/main/kotlin/com/example/domain/repository/IShoppingListRepository.kt << 'KOTLIN'
package com.example.domain.repository

import com.example.domain.model.ShoppingList
import kotlinx.coroutines.flow.Flow

interface IShoppingListRepository {
    fun observeAllLists(): Flow<List<ShoppingList>>
    suspend fun getListById(id: Long): ShoppingList?
    suspend fun createList(list: ShoppingList): Long
    suspend fun updateList(list: ShoppingList)
    suspend fun deleteList(id: Long)
    fun observeCompletedLists(): Flow<List<ShoppingList>>
}
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/repository/IShoppingItemRepository.kt << 'KOTLIN'
package com.example.domain.repository

import com.example.domain.model.ShoppingItem
import kotlinx.coroutines.flow.Flow

interface IShoppingItemRepository {
    fun observeItemsByListId(listId: Long): Flow<List<ShoppingItem>>
    suspend fun getItemById(id: Long): ShoppingItem?
    suspend fun addItem(item: ShoppingItem): Long
    suspend fun updateItem(item: ShoppingItem)
    suspend fun deleteItem(id: Long)
    suspend fun checkItem(id: Long, isChecked: Boolean)
    suspend fun deleteAllItemsInList(listId: Long)
}
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/repository/ICategoryRepository.kt << 'KOTLIN'
package com.example.domain.repository

import com.example.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {
    fun observeAll(): Flow<List<Category>>
    suspend fun create(category: Category): Long
    suspend fun delete(id: Long)
}
KOTLIN

# domain - usecases list
cat > domain/src/main/kotlin/com/example/domain/usecase/list/GetAllListsUseCase.kt << 'KOTLIN'
package com.example.domain.usecase.list

import com.example.domain.model.ShoppingList
import com.example.domain.repository.IShoppingListRepository
import kotlinx.coroutines.flow.Flow

class GetAllListsUseCase(
    private val repository: IShoppingListRepository
) {
    operator fun invoke(): Flow<List<ShoppingList>> =
        repository.observeAllLists()
}
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/usecase/list/CreateListUseCase.kt << 'KOTLIN'
package com.example.domain.usecase.list

import com.example.domain.model.ShoppingList
import com.example.domain.repository.IShoppingListRepository

class CreateListUseCase(
    private val repository: IShoppingListRepository
) {
    suspend operator fun invoke(name: String): Long {
        if (name.isBlank()) throw IllegalArgumentException("List name cannot be empty")
        if (name.length > 100) throw IllegalArgumentException("List name is too long")
        return repository.createList(ShoppingList(name = name.trim()))
    }
}
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/usecase/list/DeleteListUseCase.kt << 'KOTLIN'
package com.example.domain.usecase.list

import com.example.domain.repository.IShoppingItemRepository
import com.example.domain.repository.IShoppingListRepository

class DeleteListUseCase(
    private val listRepository: IShoppingListRepository,
    private val itemRepository: IShoppingItemRepository
) {
    suspend operator fun invoke(listId: Long) {
        itemRepository.deleteAllItemsInList(listId)
        listRepository.deleteList(listId)
    }
}
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/usecase/list/CompleteListUseCase.kt << 'KOTLIN'
package com.example.domain.usecase.list

import com.example.domain.repository.IShoppingListRepository

class CompleteListUseCase(
    private val repository: IShoppingListRepository
) {
    suspend operator fun invoke(listId: Long) {
        val list = repository.getListById(listId)
            ?: throw IllegalArgumentException("List not found")
        repository.updateList(list.copy(isCompleted = true))
    }
}
KOTLIN

# domain - usecases item
cat > domain/src/main/kotlin/com/example/domain/usecase/item/GetItemsByListUseCase.kt << 'KOTLIN'
package com.example.domain.usecase.item

import com.example.domain.model.ShoppingItem
import com.example.domain.repository.IShoppingItemRepository
import kotlinx.coroutines.flow.Flow

class GetItemsByListUseCase(
    private val repository: IShoppingItemRepository
) {
    operator fun invoke(listId: Long): Flow<List<ShoppingItem>> =
        repository.observeItemsByListId(listId)
}
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/usecase/item/AddItemUseCase.kt << 'KOTLIN'
package com.example.domain.usecase.item

import com.example.domain.model.ShoppingItem
import com.example.domain.repository.IShoppingItemRepository

class AddItemUseCase(
    private val repository: IShoppingItemRepository
) {
    suspend operator fun invoke(listId: Long, name: String, quantity: String = "1", categoryId: Long? = null): Long {
        if (name.isBlank()) throw IllegalArgumentException("Item name cannot be empty")
        val item = ShoppingItem(
            listId = listId,
            name = name.trim(),
            quantity = quantity.trim().ifBlank { "1" },
            categoryId = categoryId
        )
        return repository.addItem(item)
    }
}
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/usecase/item/CheckItemUseCase.kt << 'KOTLIN'
package com.example.domain.usecase.item

import com.example.domain.repository.IShoppingItemRepository

class CheckItemUseCase(
    private val repository: IShoppingItemRepository
) {
    suspend operator fun invoke(itemId: Long, isChecked: Boolean) {
        repository.checkItem(itemId, isChecked)
    }
}
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/usecase/item/DeleteItemUseCase.kt << 'KOTLIN'
package com.example.domain.usecase.item

import com.example.domain.repository.IShoppingItemRepository

class DeleteItemUseCase(
    private val repository: IShoppingItemRepository
) {
    suspend operator fun invoke(itemId: Long) {
        repository.deleteItem(itemId)
    }
}
KOTLIN

# domain - usecases category
cat > domain/src/main/kotlin/com/example/domain/usecase/category/GetAllCategoriesUseCase.kt << 'KOTLIN'
package com.example.domain.usecase.category

import com.example.domain.model.Category
import com.example.domain.repository.ICategoryRepository
import kotlinx.coroutines.flow.Flow

class GetAllCategoriesUseCase(
    private val repository: ICategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> = repository.observeAll()
}
KOTLIN

cat > domain/src/main/kotlin/com/example/domain/usecase/category/CreateCategoryUseCase.kt << 'KOTLIN'
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
KOTLIN

# core
cat > core/src/main/kotlin/com/example/core/util/Resource.kt << 'KOTLIN'
package com.example.core.util

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val message: String) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}
KOTLIN

cat > core/src/main/kotlin/com/example/core/util/Extensions.kt << 'KOTLIN'
package com.example.core.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.asResource(): Flow<Resource<T>> =
    this.map<T, Resource<T>> { Resource.Success(it) }
        .catch { emit(Resource.Error(it.message ?: "Unknown error")) }
KOTLIN

cat > core/src/main/kotlin/com/example/core/base/BaseViewModel.kt << 'KOTLIN'
package com.example.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {
    protected fun launchSafe(onError: (Throwable) -> Unit = {}, block: suspend () -> Unit) {
        val handler = CoroutineExceptionHandler { _, throwable -> onError(throwable) }
        viewModelScope.launch(handler) { block() }
    }
}
KOTLIN

# data - entities
cat > data/src/main/kotlin/com/example/data/db/entity/ShoppingListEntity.kt << 'KOTLIN'
package com.example.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "is_completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long
)
KOTLIN

cat > data/src/main/kotlin/com/example/data/db/entity/ShoppingItemEntity.kt << 'KOTLIN'
package com.example.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "shopping_items",
    foreignKeys = [ForeignKey(
        entity = ShoppingListEntity::class,
        parentColumns = ["id"],
        childColumns = ["list_id"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class ShoppingItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "list_id", index = true) val listId: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "quantity") val quantity: String,
    @ColumnInfo(name = "category_id") val categoryId: Long?,
    @ColumnInfo(name = "is_checked") val isChecked: Boolean = false
)
KOTLIN

cat > data/src/main/kotlin/com/example/data/db/entity/CategoryEntity.kt << 'KOTLIN'
package com.example.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "color_hex") val colorHex: String
)
KOTLIN

# data - dao
cat > data/src/main/kotlin/com/example/data/db/dao/ShoppingListDao.kt << 'KOTLIN'
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
KOTLIN

cat > data/src/main/kotlin/com/example/data/db/dao/ShoppingItemDao.kt << 'KOTLIN'
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
KOTLIN

cat > data/src/main/kotlin/com/example/data/db/dao/CategoryDao.kt << 'KOTLIN'
package com.example.data.db.dao

import androidx.room.*
import com.example.data.db.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: CategoryEntity): Long

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteById(id: Long)
}
KOTLIN

# data - AppDatabase
cat > data/src/main/kotlin/com/example/data/db/AppDatabase.kt << 'KOTLIN'
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
KOTLIN

# data - mapper
cat > data/src/main/kotlin/com/example/data/mapper/Mappers.kt << 'KOTLIN'
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
KOTLIN

# data - repositories
cat > data/src/main/kotlin/com/example/data/repository/ShoppingListRepositoryImpl.kt << 'KOTLIN'
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
KOTLIN

cat > data/src/main/kotlin/com/example/data/repository/ShoppingItemRepositoryImpl.kt << 'KOTLIN'
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
KOTLIN

cat > data/src/main/kotlin/com/example/data/repository/CategoryRepositoryImpl.kt << 'KOTLIN'
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
KOTLIN

# data - di
cat > data/src/main/kotlin/com/example/data/di/DatabaseModule.kt << 'KOTLIN'
package com.example.data.di

import android.content.Context
import androidx.room.Room
import com.example.data.db.AppDatabase
import com.example.data.db.dao.CategoryDao
import com.example.data.db.dao.ShoppingItemDao
import com.example.data.db.dao.ShoppingListDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "shopping_list.db").build()

    @Provides fun provideListDao(db: AppDatabase): ShoppingListDao = db.shoppingListDao()
    @Provides fun provideItemDao(db: AppDatabase): ShoppingItemDao = db.shoppingItemDao()
    @Provides fun provideCategoryDao(db: AppDatabase): CategoryDao = db.categoryDao()
}
KOTLIN

cat > data/src/main/kotlin/com/example/data/di/RepositoryModule.kt << 'KOTLIN'
package com.example.data.di

import com.example.data.repository.CategoryRepositoryImpl
import com.example.data.repository.ShoppingItemRepositoryImpl
import com.example.data.repository.ShoppingListRepositoryImpl
import com.example.domain.repository.ICategoryRepository
import com.example.domain.repository.IShoppingItemRepository
import com.example.domain.repository.IShoppingListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindListRepo(impl: ShoppingListRepositoryImpl): IShoppingListRepository
    @Binds @Singleton
    abstract fun bindItemRepo(impl: ShoppingItemRepositoryImpl): IShoppingItemRepository
    @Binds @Singleton
    abstract fun bindCategoryRepo(impl: CategoryRepositoryImpl): ICategoryRepository
}
KOTLIN

# konsist tests
cat > konsist-tests/src/test/kotlin/ArchitectureTest.kt << 'KOTLIN'
import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.assertTrue
import com.lemonappdev.konsist.api.ext.list.withNameEndingWith
import org.junit.Test

class ArchitectureTest {

    @Test
    fun `domain does not depend on Android framework`() {
        Konsist
            .scopeFromModule("domain")
            .files
            .assertTrue {
                it.imports.none { import -> import.name.startsWith("android.") }
            }
    }

    @Test
    fun `data does not depend on UI components`() {
        Konsist
            .scopeFromModule("data")
            .files
            .assertTrue {
                it.imports.none { import ->
                    import.name.startsWith("androidx.compose") ||
                    import.name.startsWith("android.widget") ||
                    import.name.startsWith("androidx.fragment")
                }
            }
    }

    @Test
    fun `use cases reside in domain usecase package`() {
        Konsist
            .scopeFromProject()
            .classes()
            .withNameEndingWith("UseCase")
            .assertTrue { it.resideInPackage("..domain..usecase..") }
    }

    @Test
    fun `repositories in domain are interfaces`() {
        Konsist
            .scopeFromModule("domain")
            .interfaces()
            .withNameEndingWith("Repository")
            .assertTrue { it.resideInPackage("..domain..repository..") }
    }

    @Test
    fun `repository implementations reside in data module`() {
        Konsist
            .scopeFromModule("data")
            .classes()
            .withNameEndingWith("RepositoryImpl")
            .assertTrue { it.resideInPackage("..data..repository..") }
    }
}
KOTLIN

echo "✅ Все файлы созданы!"
