mkdir -p app/src/main/kotlin/com/example/shoppinglistapp/di
mkdir -p app/src/main/kotlin/com/example/shoppinglistapp/ui/lists
mkdir -p app/src/main/kotlin/com/example/shoppinglistapp/ui/items

cat > app/src/main/kotlin/com/example/shoppinglistapp/ShoppingApp.kt << 'KOTLIN'
package com.example.shoppinglistapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ShoppingApp : Application()
KOTLIN

cat > app/src/main/kotlin/com/example/shoppinglistapp/di/UseCaseModule.kt << 'KOTLIN'
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
KOTLIN

cat > app/src/main/kotlin/com/example/shoppinglistapp/ui/lists/ShoppingListViewModel.kt << 'KOTLIN'
package com.example.shoppinglistapp.ui.lists

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

cat > app/src/main/kotlin/com/example/shoppinglistapp/ui/lists/ShoppingListScreen.kt << 'KOTLIN'
package com.example.shoppinglistapp.ui.lists

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
        topBar = {
            TopAppBar(title = { Text("Списки покупок") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить список")
            }
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.lists.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Нет списков. Нажмите + чтобы создать", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.lists, key = { it.id }) { list ->
                    ShoppingListItem(
                        list = list,
                        onClick = { onListClick(list.id) },
                        onDelete = { viewModel.delete(list.id) },
                        onComplete = { viewModel.complete(list.id) }
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false; newListName = "" },
            title = { Text("Новый список") },
            text = {
                OutlinedTextField(
                    value = newListName,
                    onValueChange = { newListName = it },
                    label = { Text("Название") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newListName.isNotBlank()) {
                        viewModel.create(newListName)
                        newListName = ""
                        showDialog = false
                    }
                }) { Text("Создать") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; newListName = "" }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun ShoppingListItem(
    list: ShoppingList,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.titleMedium,
                textDecoration = if (list.isCompleted) TextDecoration.LineThrough else null,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onComplete) {
                Icon(Icons.Default.Check, contentDescription = "Завершить", tint = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
KOTLIN

cat > app/src/main/kotlin/com/example/shoppinglistapp/ui/items/ShoppingItemViewModel.kt << 'KOTLIN'
package com.example.shoppinglistapp.ui.items

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

cat > app/src/main/kotlin/com/example/shoppinglistapp/ui/items/ShoppingItemScreen.kt << 'KOTLIN'
package com.example.shoppinglistapp.ui.items

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
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
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить товар")
            }
        }
    ) { padding ->
        if (uiState.items.isEmpty()) {
            Box(
                Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Список пуст. Нажмите + чтобы добавить товар")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.items, key = { it.id }) { item ->
                    ShoppingItemCard(
                        item = item,
                        onCheck = { viewModel.check(item.id, !item.isChecked) },
                        onDelete = { viewModel.delete(item.id) }
                    )
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
                    OutlinedTextField(
                        value = newItemName,
                        onValueChange = { newItemName = it },
                        label = { Text("Название") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newItemQty,
                        onValueChange = { newItemQty = it },
                        label = { Text("Количество") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newItemName.isNotBlank()) {
                        viewModel.add(newItemName, newItemQty)
                        newItemName = ""
                        newItemQty = "1"
                        showDialog = false
                    }
                }) { Text("Добавить") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false; newItemName = "" }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun ShoppingItemCard(
    item: ShoppingItem,
    onCheck: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = item.isChecked, onCheckedChange = { onCheck() })
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (item.isChecked) TextDecoration.LineThrough else null
                )
                Text(
                    text = "Кол-во: ${item.quantity}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
KOTLIN

cat > app/src/main/kotlin/com/example/shoppinglistapp/MainActivity.kt << 'KOTLIN'
package com.example.shoppinglistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoppinglistapp.ui.items.ShoppingItemScreen
import com.example.shoppinglistapp.ui.lists.ShoppingListScreen
import com.example.shoppinglistapp.ui.theme.ShoppingListAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShoppingListAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "lists") {
        composable("lists") {
            ShoppingListScreen(
                onListClick = { listId -> navController.navigate("items/$listId") }
            )
        }
        composable(
            route = "items/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.LongType })
        ) {
            ShoppingItemScreen(onBack = { navController.popBackStack() })
        }
    }
}
KOTLIN

echo "✅ UI файлы созданы!"
