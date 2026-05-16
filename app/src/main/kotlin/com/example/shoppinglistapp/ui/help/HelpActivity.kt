package com.example.shoppinglistapp.ui.help

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.example.shoppinglistapp.R

class HelpActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        findViewById<ComposeView>(R.id.composeView).setContent {
            MaterialTheme {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Как пользоваться приложением",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("1. Нажмите + чтобы создать новый список покупок")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("2. Откройте список и добавьте товары")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("3. Отмечайте купленные товары галочкой")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("4. Завершите список когда все куплено")
                }
            }
        }
    }
}
