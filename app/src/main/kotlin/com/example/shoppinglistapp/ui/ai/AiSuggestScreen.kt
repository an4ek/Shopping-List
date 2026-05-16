package com.example.shoppinglistapp.ui.ai

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shoppinglistapp.ai.GeminiService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiSuggestScreen(
    onBack: () -> Unit,
    onAddItems: (List<String>) -> Unit
) {
    val scope = rememberCoroutineScope()
    val geminiService = remember { GeminiService() }

    var prompt by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var error by remember { mutableStateOf<String?>(null) }
    val selected = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI-помощник") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = selected.isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { onAddItems(selected.toList()) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Добавить выбранные (${selected.size})")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Введите блюдо или событие, и AI предложит список продуктов",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                label = { Text("Например: ужин на двоих, борщ, пикник") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    if (prompt.isNotBlank()) {
                        scope.launch {
                            isLoading = true
                            error = null
                            suggestions = emptyList()
                            selected.clear()
                            val result = geminiService.suggestItems(prompt)
                            if (result.isEmpty()) {
                                error = "Не удалось получить ответ. Проверьте подключение."
                            } else {
                                suggestions = result
                            }
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && prompt.isNotBlank()
            ) {
                Text(if (isLoading) "Генерирую..." else "Предложить продукты")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            error?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }

            if (suggestions.isNotEmpty()) {
                Text(
                    text = "Выберите что добавить в список:",
                    style = MaterialTheme.typography.titleSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(suggestions) { item ->
                        val isChecked = item in selected
                        Card(
                            onClick = {
                                if (isChecked) selected.remove(item)
                                else selected.add(item)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isChecked)
                                    MaterialTheme.colorScheme.primaryContainer
                                else
                                    MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(checked = isChecked, onCheckedChange = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(item)
                            }
                        }
                    }
                }
            }
        }
    }
}
