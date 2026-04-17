package com.example.shoppinglistapp.di

import com.example.domain.repository.ICategoryRepository
import com.example.domain.repository.IShoppingItemRepository
import com.example.domain.repository.IShoppingListRepository
import com.example.domain.usecase.category.CreateCategoryUseCase
import com.example.domain.usecase.category.GetAllCategoriesUseCase
import com.example.domain.usecase.item.AddItemUseCase
import com.example.domain.usecase.item.CheckItemUseCase
import com.example.domain.usecase.item.DeleteItemUseCase
import com.example.domain.usecase.item.GetItemsByListUseCase
import com.example.domain.usecase.list.CompleteListUseCase
import com.example.domain.usecase.list.CreateListUseCase
import com.example.domain.usecase.list.DeleteListUseCase
import com.example.domain.usecase.list.GetAllListsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    @Provides fun provideGetAllLists(r: IShoppingListRepository) = GetAllListsUseCase(r)
    @Provides fun provideCreateList(r: IShoppingListRepository) = CreateListUseCase(r)
    @Provides fun provideDeleteList(lr: IShoppingListRepository, ir: IShoppingItemRepository) = DeleteListUseCase(lr, ir)
    @Provides fun provideCompleteList(r: IShoppingListRepository) = CompleteListUseCase(r)
    @Provides fun provideGetItems(r: IShoppingItemRepository) = GetItemsByListUseCase(r)
    @Provides fun provideAddItem(r: IShoppingItemRepository) = AddItemUseCase(r)
    @Provides fun provideCheckItem(r: IShoppingItemRepository) = CheckItemUseCase(r)
    @Provides fun provideDeleteItem(r: IShoppingItemRepository) = DeleteItemUseCase(r)
    @Provides fun provideGetCategories(r: ICategoryRepository) = GetAllCategoriesUseCase(r)
    @Provides fun provideCreateCategory(r: ICategoryRepository) = CreateCategoryUseCase(r)
}
