package com.example.shoppinglistapp.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.ShoppingList
import com.example.domain.usecase.list.CompleteListUseCase
import com.example.domain.usecase.list.CreateListUseCase
import com.example.domain.usecase.list.DeleteListUseCase
import com.example.domain.usecase.list.GetAllListsUseCase
import com.example.shoppinglistapp.analytics.AnalyticsService
import com.example.shoppinglistapp.config.RemoteConfigService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ShoppingListUiState(
    val lists: List<ShoppingList> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val getAllListsUseCase: GetAllListsUseCase,
    private val createListUseCase: CreateListUseCase,
    private val deleteListUseCase: DeleteListUseCase,
    private val completeListUseCase: CompleteListUseCase,
    private val analytics: AnalyticsService,
    private val remoteConfig: RemoteConfigService
) : ViewModel() {

    private val _uiState = MutableStateFlow(ShoppingListUiState())
    val uiState: StateFlow<ShoppingListUiState> = _uiState

    private val _welcomeBannerText = MutableStateFlow("Добро пожаловать в Список покупок!")
    val welcomeBannerText: StateFlow<String> = _welcomeBannerText

    private val _showPromoBanner = MutableStateFlow(false)
    val showPromoBanner: StateFlow<Boolean> = _showPromoBanner

    init {
        loadLists()
        fetchRemoteConfig()
    }

    private fun loadLists() {
        viewModelScope.launch {
            getAllListsUseCase()
                .catch { e -> _uiState.value = ShoppingListUiState(error = e.message, isLoading = false) }
                .collect { lists -> _uiState.value = ShoppingListUiState(lists = lists, isLoading = false) }
        }
    }

    private fun fetchRemoteConfig() {
        viewModelScope.launch {
            remoteConfig.fetchAndActivate()
            _welcomeBannerText.value = remoteConfig.getWelcomeBannerText()
            _showPromoBanner.value = remoteConfig.isPromoBannerEnabled()
        }
    }

    fun onScreenViewed() {
        analytics.trackEvent("screen_viewed", mapOf("screen_name" to "shopping_lists"))
    }

    fun create(name: String) {
        viewModelScope.launch {
            createListUseCase(name)
            analytics.trackEvent("list_created", mapOf("list_name" to name))
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            deleteListUseCase(id)
            analytics.trackEvent("list_deleted", mapOf("list_id" to id.toString()))
        }
    }

    fun complete(id: Long) {
        viewModelScope.launch {
            completeListUseCase(id)
            analytics.trackEvent("list_completed", mapOf("list_id" to id.toString()))
        }
    }
}
