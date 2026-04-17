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
