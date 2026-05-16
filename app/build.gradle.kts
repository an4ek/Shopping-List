import java.util.Properties
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}
android {
    namespace = "com.example.shoppinglistapp"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.example.shoppinglistapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "APPMETRICA_API_KEY", "\"${localProperties["appmetrica_api_key"]}\"")
        buildConfigField("String", "VK_APP_ID", "\"${localProperties["vk_app_id"]}\"")
        buildConfigField("String", "YANDEX_CLIENT_ID", "\"${localProperties["yandex_client_id"]}\"")
        buildConfigField("String", "MAPKIT_API_KEY", "\"${localProperties["mapkit_api_key"]}\"")
        manifestPlaceholders["YANDEX_CLIENT_ID"] = localProperties["yandex_client_id"] ?: ""
        manifestPlaceholders["VK_APP_ID"] = localProperties["vk_app_id"] ?: ""
    }
    flavorDimensions += "env"
    productFlavors {
        create("dev") {

            versionNameSuffix = "-dev"
            buildConfigField("String", "BASE_URL", "\"https://dev.api.shoppinglist.com\"")
        }
        create("prod") {
            buildConfigField("String", "BASE_URL", "\"https://api.shoppinglist.com\"")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}
dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":core"))
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.coroutines.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.navigation.compose)
    implementation(libs.work.runtime)
    implementation("io.appmetrica.analytics:analytics:7.+")
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("com.vk:android-sdk-core:4.1.0")
    implementation("com.vk:android-sdk-api:4.1.0")
    implementation("com.yandex.android:authsdk:3.1.3")
    implementation("com.yandex.android:maps.mobile:4.6.1-lite")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-config")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-crashlytics")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}