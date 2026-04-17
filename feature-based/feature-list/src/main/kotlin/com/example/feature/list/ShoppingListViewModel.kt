package com.example.feature.list

import androidx.lifecycle.viewModelScope
import com.example.core.base.BaseViewModel
import com.example.domain.model.ShoppingList
import com.example.domain.usecase.list.CompleteListUseCase
import com.example.domain.usecase.list.CreateListUseCase
import com.example.domain.usecase.list.DeleteListUseCase
import com.example.domain.usecase.list.GetAllListsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ListsUiState(
    val lists: List<ShoppingList> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val getAllLists: GetAllListsUseCase,
    private val createList: CreateListUseCase,
    private val deleteList: DeleteListUseCase,
    private val completeList: CompleteListUseCase
) : BaseViewModel() {

    val uiState: StateFlow<ListsUiState> = getAllLists()
        .map { ListsUiState(lists = it) }
        .catch { emit(ListsUiState(error = it.message)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListsUiState(isLoading = true))

    fun create(name: String) = launchSafe { createList(name) }
    fun delete(id: Long) = launchSafe { deleteList(id) }
    fun complete(id: Long) = launchSafe { completeList(id) }
}
