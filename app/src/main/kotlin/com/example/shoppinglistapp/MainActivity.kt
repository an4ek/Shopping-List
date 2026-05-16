package com.example.shoppinglistapp

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoppinglistapp.auth.AuthServiceImpl
import com.example.shoppinglistapp.ui.about.AboutScreen
import com.example.shoppinglistapp.ui.ai.AiSuggestScreen
import com.example.shoppinglistapp.ui.auth.LoginScreen
import com.example.shoppinglistapp.ui.auth.LoginViewModel
import com.example.shoppinglistapp.ui.items.ShoppingItemScreen
import com.example.shoppinglistapp.ui.lists.ShoppingListScreen
import com.example.shoppinglistapp.ui.theme.ShoppingListAppTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authService: com.example.shoppinglistapp.auth.AuthService

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        android.util.Log.d("MainActivity", "POST_NOTIFICATIONS granted: $isGranted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestNotificationPermission()
        (authService as? AuthServiceImpl)?.registerYandexLauncher(this)
        setContent {
            ShoppingListAppTheme {
                AppNavigation()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = hiltViewModel()
    val startDestination = if (loginViewModel.isLoggedIn()) "lists" else "login"

    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("lists") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("lists") {
            ShoppingListScreen(
                onListClick = { listId -> navController.navigate("items/$listId") },
                onAboutClick = { navController.navigate("about") },
                onAiClick = { navController.navigate("lists_ai") }
            )
        }
        composable(
            route = "items/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.LongType })
        ) { backStackEntry ->
            val listId = backStackEntry.arguments?.getLong("listId") ?: return@composable
            ShoppingItemScreen(
                onBack = { navController.popBackStack() },
                onAiClick = { navController.navigate("items_ai/$listId") },
                onAiItemsReceived = { callback ->
                    val items = backStackEntry.savedStateHandle.get<ArrayList<String>>("ai_items")
                    if (!items.isNullOrEmpty()) {
                        callback(items)
                        backStackEntry.savedStateHandle.remove<ArrayList<String>>("ai_items")
                    }
                }
            )
        }
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
        composable("lists_ai") {
            AiSuggestScreen(
                onBack = { navController.popBackStack() },
                onAddItems = { navController.popBackStack() }
            )
        }
        composable(
            route = "items_ai/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.LongType })
        ) {
            AiSuggestScreen(
                onBack = { navController.popBackStack() },
                onAddItems = { items ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("ai_items", ArrayList(items))
                    navController.popBackStack()
                }
            )
        }
    }
}
