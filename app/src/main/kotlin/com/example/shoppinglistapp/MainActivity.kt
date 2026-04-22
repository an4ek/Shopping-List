package com.example.shoppinglistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoppinglistapp.ui.about.AboutScreen
import com.example.shoppinglistapp.ui.auth.LoginScreen
import com.example.shoppinglistapp.ui.auth.LoginViewModel
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
                onAboutClick = { navController.navigate("about") }
            )
        }
        composable(
            route = "items/{listId}",
            arguments = listOf(navArgument("listId") { type = NavType.LongType })
        ) {
            ShoppingItemScreen(onBack = { navController.popBackStack() })
        }
        composable("about") {
            AboutScreen(onBack = { navController.popBackStack() })
        }
    }
}
