package com.example.shoppinglistapp.ui.about

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

private const val OFFICE_LAT = 55.753544
private const val OFFICE_LON = 37.621202
private const val OFFICE_ADDRESS = "г. Москва, Красная площадь, 1"
private const val OFFICE_NAME = "ООО ШопингЛист"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(onBack: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("О нас") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(OFFICE_NAME, style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Мы — команда энтузиастов, которые помогают людям не забывать " +
                    "нужные вещи в магазине. Приложение создано с любовью и заботой.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Адрес офиса: $OFFICE_ADDRESS",
                    style = MaterialTheme.typography.bodySmall
                )
            }

            AndroidView(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                        loadUrl(
                            "https://maps.yandex.ru/?ll=$OFFICE_LON,$OFFICE_LAT&z=15&pt=$OFFICE_LON,$OFFICE_LAT,pm2rdm"
                        )
                    }
                }
            )

            Button(
                onClick = {
                    val uri = Uri.parse(
                        "yandexmaps://maps.yandex.ru/?rtext=~$OFFICE_LAT,$OFFICE_LON&rtt=pd"
                    )
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    if (intent.resolveActivity(context.packageManager) != null) {
                        context.startActivity(intent)
                    } else {
                        val webUri = Uri.parse(
                            "https://maps.yandex.ru/?rtext=~$OFFICE_LAT,$OFFICE_LON"
                        )
                        context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Text("Построить маршрут до офиса")
            }
        }
    }
}
