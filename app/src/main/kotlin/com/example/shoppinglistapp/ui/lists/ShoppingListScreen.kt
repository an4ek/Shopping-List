package com.example.shoppinglistapp.ui.lists

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.domain.model.ShoppingList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    onListClick: (Long) -> Unit,
    onAboutClick: () -> Unit,
    onAiClick: () -> Unit,
    viewModel: ShoppingListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val welcomeText by viewModel.welcomeBannerText.collectAsState()
    val showPromo by viewModel.showPromoBanner.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showCrashDialog by remember { mutableStateOf(false) }
    var newListName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.onScreenViewed()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Списки покупок") },
                actions = {
                    IconButton(onClick = onAiClick) {
                        Icon(Icons.Default.Add, contentDescription = "AI-помощник")
                    }
                    IconButton(onClick = { showCrashDialog = true }) {
                        Icon(Icons.Default.Warning, contentDescription = "Тест краша", tint = MaterialTheme.colorScheme.error)
                    }
                    IconButton(onClick = onAboutClick) {
                        Icon(Icons.Default.Info, contentDescription = "О нас")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Добавить список")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Text(text = welcomeText, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
            }

            AnimatedVisibility(
                visible = showPromo,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
                ) {
                    Text(text = "🎉 Специальное предложение!", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.lists.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Нет списков. Нажмите + чтобы создать")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
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
    }

    if (showCrashDialog) {
        AlertDialog(
            onDismissRequest = { showCrashDialog = false },
            title = { Text("Тест краш-репортинга") },
            text = { Text("Выберите тип ошибки для отправки в Crashlytics и AppMetrica") },
            confirmButton = {
                TextButton(onClick = {
                    showCrashDialog = false
                    viewModel.generateCrash()
                }) { Text("Fatal crash", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = {
                    showCrashDialog = false
                    viewModel.generateNonFatal()
                }) { Text("Non-fatal") }
            }
        )
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
                TextButton(onClick = { showDialog = false; newListName = "" }) { Text("Отмена") }
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
    val cardColor by animateColorAsState(
        targetValue = if (list.isCompleted)
            MaterialTheme.colorScheme.surfaceVariant
        else
            MaterialTheme.colorScheme.surface,
        label = "cardColor"
    )

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
