package com.example.presentation.items

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.core.base.BaseViewModel
import com.example.domain.model.ShoppingItem
import com.example.domain.usecase.item.AddItemUseCase
import com.example.domain.usecase.item.CheckItemUseCase
import com.example.domain.usecase.item.DeleteItemUseCase
import com.example.domain.usecase.item.GetItemsByListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

data class ItemsUiState(
    val items: List<ShoppingItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ShoppingItemViewModel @Inject constructor(
    private val getItems: GetItemsByListUseCase,
    private val addItem: AddItemUseCase,
    private val checkItem: CheckItemUseCase,
    private val deleteItem: DeleteItemUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel() {

    private val listId: Long = checkNotNull(savedStateHandle["listId"])

    val uiState: StateFlow<ItemsUiState> = getItems(listId)
        .map { ItemsUiState(items = it) }
        .catch { emit(ItemsUiState(error = it.message)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ItemsUiState(isLoading = true))

    fun add(name: String, quantity: String = "1") = launchSafe { addItem(listId, name, quantity) }
    fun check(itemId: Long, isChecked: Boolean) = launchSafe { checkItem(itemId, isChecked) }
    fun delete(itemId: Long) = launchSafe { deleteItem(itemId) }
}
