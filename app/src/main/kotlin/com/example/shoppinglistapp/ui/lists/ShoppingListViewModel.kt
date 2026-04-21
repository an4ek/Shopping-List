package com.example.shoppinglistapp.ui.lists

import androidx.lifecycle.viewModelScope
import com.example.core.base.BaseViewModel
import com.example.domain.model.ShoppingList
import com.example.domain.usecase.list.CompleteListUseCase
import com.example.domain.usecase.list.CreateListUseCase
import com.example.domain.usecase.list.DeleteListUseCase
import com.example.domain.usecase.list.GetAllListsUseCase
import com.example.shoppinglistapp.analytics.AnalyticsService
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
    private val completeList: CompleteListUseCase,
    private val analytics: AnalyticsService
) : BaseViewModel() {

    val uiState: StateFlow<ListsUiState> = getAllLists()
        .map { ListsUiState(lists = it) }
        .catch { emit(ListsUiState(error = it.message)) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListsUiState(isLoading = true))

    fun onScreenViewed() {
        analytics.trackEvent("screen_viewed", mapOf("screen_name" to "shopping_lists"))
    }

    fun create(name: String) = launchSafe {
        createList(name)
        analytics.trackEvent("list_created", mapOf("list_name" to name))
    }

    fun delete(id: Long) = launchSafe {
        deleteList(id)
        analytics.trackEvent("list_deleted", mapOf("list_id" to id))
    }

    fun complete(id: Long) = launchSafe {
        completeList(id)
        analytics.trackEvent("list_completed", mapOf("list_id" to id))
    }
}
