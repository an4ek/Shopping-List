# ==================== LAYER-BASED ====================
mkdir -p layer-based/presentation/src/main/kotlin/com/example/presentation/lists
mkdir -p layer-based/presentation/src/main/kotlin/com/example/presentation/items

cat > layer-based/presentation/build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.presentation"
    compileSdk = 34
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.coroutines.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
}
GRADLE

# ==================== FEATURE-BASED ====================
# core:navigation
mkdir -p feature-based/core-navigation/src/main/kotlin/com/example/core/navigation

cat > feature-based/core-navigation/build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.core.navigation"
    compileSdk = 34
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
}
GRADLE

cat > feature-based/core-navigation/src/main/kotlin/com/example/core/navigation/AppRoutes.kt << 'KOTLIN'
package com.example.core.navigation

object AppRoutes {
    const val LISTS = "lists"
    const val ITEMS = "items/{listId}"
    fun items(listId: Long) = "items/$listId"
}
KOTLIN

# feature:list
mkdir -p feature-based/feature-list/src/main/kotlin/com/example/feature/list

cat > feature-based/feature-list/build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.feature.list"
    compileSdk = 34
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.coroutines.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
GRADLE

cat > feature-based/feature-list/src/main/kotlin/com/example/feature/list/ShoppingListViewModel.kt << 'KOTLIN'
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
KOTLIN

cat > feature-based/feature-list/src/main/kotlin/com/example/feature/list/ShoppingListScreen.kt << 'KOTLIN'
package com.example.feature.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.model.ShoppingList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    onListClick: (Long) -> Unit,
    viewModel: ShoppingListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Списки покупок") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить")
            }
        }
    ) { padding ->
        if (uiState.lists.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Нет списков. Нажмите + чтобы создать")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.lists, key = { it.id }) { list ->
                    ShoppingListItem(list, { onListClick(list.id) }, { viewModel.delete(list.id) }, { viewModel.complete(list.id) })
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; newListName = "" },
            title = { Text("Новый список") },
            text = {
                OutlinedTextField(value = newListName, onValueChange = { newListName = it }, label = { Text("Название") }, singleLine = true)
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newListName.isNotBlank()) { viewModel.create(newListName); newListName = ""; showDialog = false }
                }) { Text("Создать") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; newListName = "" }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun ShoppingListItem(list: ShoppingList, onClick: () -> Unit, onDelete: () -> Unit, onComplete: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (list.isCompleted) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onComplete) { Icon(Icons.Default.Check, contentDescription = "Завершить", tint = MaterialTheme.colorScheme.primary) }
            IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error) }
        }
    }
}
KOTLIN

# feature:items
mkdir -p feature-based/feature-items/src/main/kotlin/com/example/feature/items

cat > feature-based/feature-items/build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.feature.items"
    compileSdk = 34
    defaultConfig { minSdk = 24 }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures { compose = true }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.coroutines.android)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.material3)
}
GRADLE

cat > feature-based/feature-items/src/main/kotlin/com/example/feature/items/ShoppingItemViewModel.kt << 'KOTLIN'
package com.example.feature.items

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
KOTLIN

cat > feature-based/feature-items/src/main/kotlin/com/example/feature/items/ShoppingItemScreen.kt << 'KOTLIN'
package com.example.feature.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.domain.model.ShoppingItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingItemScreen(
    onBack: () -> Unit,
    viewModel: ShoppingItemViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newItemName by remember { mutableStateOf("") }
    var newItemQty by remember { mutableStateOf("1") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Товары") },
                navigationIcon = { TextButton(onClick = onBack) { Text("Назад") } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить товар")
            }
        }
    ) { padding ->
        if (uiState.items.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Список пуст. Нажмите + чтобы добавить товар")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    ShoppingItemCard(item, { viewModel.check(item.id, !item.isChecked) }, { viewModel.delete(item.id) })
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; newItemName = "" },
            title = { Text("Новый товар") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = newItemName, onValueChange = { newItemName = it }, label = { Text("Название") }, singleLine = true)
                    OutlinedTextField(value = newItemQty, onValueChange = { newItemQty = it }, label = { Text("Количество") }, singleLine = true)
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newItemName.isNotBlank()) { viewModel.add(newItemName, newItemQty); newItemName = ""; newItemQty = "1"; showDialog = false }
                }) { Text("Добавить") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; newItemName = "" }) { Text("Отмена") }
            }
        )
    }
}

@Composable
fun ShoppingItemCard(item: ShoppingItem, onCheck: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = item.isChecked, onCheckedChange = { onCheck() })
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
                )
                Text(text = "Кол-во: ${item.quantity}", style = MaterialTheme.typography.bodySmall)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
KOTLIN

echo "✅ Структура для веток создана!"
