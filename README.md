# Shopping List App

Мобильное приложение для управления списками покупок на Jetpack Compose.

## Скриншоты

> Добавьте скриншоты приложения после запуска на устройстве/эмуляторе.

## Функциональность

- Создание и управление списками покупок
- Добавление товаров с категориями
- Авторизация через VK и Яндекс ID
- AI-помощник: предлагает список продуктов по запросу (Gemini через OpenRouter)
- Push-уведомления через Firebase FCM
- Карта с адресом офиса компании
- Экран профиля с данными из Firestore

## Обязательные критерии

### Чистая архитектура
- Модули: `:app`, `:domain`, `:data`, `:core`
- Domain: `app/src/main/kotlin/com/example/domain/`
- Use Cases: `domain/src/main/kotlin/com/example/domain/usecase/`
- Репозитории (интерфейсы): `domain/src/main/kotlin/com/example/domain/repository/`
- Репозитории (реализации): `data/src/main/kotlin/com/example/data/repository/`
- DI через Hilt: `app/src/main/kotlin/com/example/shoppinglistapp/di/`

### Фоновые задачи и сервисы
- WorkManager: `app/src/main/kotlin/com/example/shoppinglistapp/worker/SyncWorker.kt`
- BroadcastReceiver: `app/src/main/kotlin/com/example/shoppinglistapp/receiver/BootReceiver.kt`
- Запуск при загрузке устройства, периодическая синхронизация каждые 15 минут с Constraints (NetworkType.CONNECTED)

### Анимации в Jetpack Compose
- `AnimatedVisibility` (промо-баннер): `app/src/main/kotlin/com/example/shoppinglistapp/ui/lists/ShoppingListScreen.kt` строки 60-72
- `animateColorAsState` (карточка списка): `app/src/main/kotlin/com/example/shoppinglistapp/ui/lists/ShoppingListScreen.kt` строки 120-126

### XML разметка и интеграция Compose
- XML layout: `app/src/main/res/layout/activity_help.xml`
- Activity с ComposeView: `app/src/main/kotlin/com/example/shoppinglistapp/ui/help/HelpActivity.kt`

### Gradle: конфигурация сборок
- buildTypes + productFlavors: `app/build.gradle.kts`
- Флейворы: `dev` (versionNameSuffix="-dev") и `prod` с разными BASE_URL
- R8/ProGuard включён в release

## Бонусные критерии

### Firebase
- FCM Push-уведомления: `app/src/main/kotlin/com/example/shoppinglistapp/push/PushMessagingService.kt`
- Remote Config: `app/src/main/kotlin/com/example/shoppinglistapp/config/FirebaseRemoteConfigService.kt`
- Firestore: `app/src/main/kotlin/com/example/shoppinglistapp/firestore/FirestoreService.kt`

### Использование ИИ
- Gemini через OpenRouter API
- Сервис: `app/src/main/kotlin/com/example/shoppinglistapp/ai/GeminiService.kt`
- Экран: `app/src/main/kotlin/com/example/shoppinglistapp/ui/ai/AiSuggestScreen.kt`
- Пользователь вводит блюдо или событие — AI предлагает список продуктов для добавления в список покупок

### Интеграция внешних сервисов
- Авторизация VK и Яндекс ID
- Firebase Crashlytics + AppMetrica с кастомными событиями
- Интерфейс CrashReporter: `app/src/main/kotlin/com/example/shoppinglistapp/crash/`

## Ветки многомодульности (Лаба 5)

- `layer-based` — UI выделен в отдельный модуль `:presentation`
- `feature-based` — фича-модули `:feature-list`, `:feature-items`, `:feature-category`, `:feature-history`
- `combined` — комбинированный подход по рекомендации Google

## Сборка

```bash
./gradlew assembleDevDebug
```

## APK

Скачать APK: [Releases](https://github.com/an4ek/Shopping-List/releases)
