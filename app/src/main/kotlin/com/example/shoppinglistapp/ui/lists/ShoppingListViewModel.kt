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
import com.example.shoppinglistapp.crash.CrashReporter
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
    private val remoteConfig: RemoteConfigService,
    private val crashReporter: CrashReporter
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
            try {
                getAllListsUseCase()
                    .catch { e ->
                        crashReporter.recordNonFatal(e)
                        _uiState.value = ShoppingListUiState(error = e.message, isLoading = false)
                    }
                    .collect { lists ->
                        _uiState.value = ShoppingListUiState(lists = lists, isLoading = false)
                    }
            } catch (e: Exception) {
                crashReporter.recordNonFatal(e)
                _uiState.value = ShoppingListUiState(error = e.message, isLoading = false)
            }
        }
    }

    private fun fetchRemoteConfig() {
        viewModelScope.launch {
            try {
                remoteConfig.fetchAndActivate()
                _welcomeBannerText.value = remoteConfig.getWelcomeBannerText()
                _showPromoBanner.value = remoteConfig.isPromoBannerEnabled()
            } catch (e: Exception) {
                crashReporter.recordNonFatal(e)
            }
        }
    }

    fun onScreenViewed() {
        crashReporter.log("ShoppingListScreen opened")
        crashReporter.setKey("screen", "shopping_lists")
        analytics.trackEvent("screen_viewed", mapOf("screen_name" to "shopping_lists"))
    }

    fun create(name: String) {
        viewModelScope.launch {
            try {
                createListUseCase(name)
                analytics.trackEvent("list_created", mapOf("list_name" to name))
            } catch (e: Exception) {
                crashReporter.recordNonFatal(e)
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            try {
                deleteListUseCase(id)
                analytics.trackEvent("list_deleted", mapOf("list_id" to id.toString()))
            } catch (e: Exception) {
                crashReporter.recordNonFatal(e)
            }
        }
    }

    fun complete(id: Long) {
        viewModelScope.launch {
            try {
                completeListUseCase(id)
                analytics.trackEvent("list_completed", mapOf("list_id" to id.toString()))
            } catch (e: Exception) {
                crashReporter.recordNonFatal(e)
            }
        }
    }

    fun generateCrash() {
        crashReporter.log("Generate crash button clicked")
        crashReporter.setKey("screen", "shopping_lists")
        crashReporter.setKey("action", "manual_crash")
        throw NullPointerException("Manual crash from control task")
    }

    fun generateNonFatal() {
        crashReporter.log("Generate non-fatal error")
        crashReporter.setKey("screen", "shopping_lists")
        crashReporter.recordNonFatal(
            IllegalStateException("Manual non-fatal error for testing")
        )
    }
}
