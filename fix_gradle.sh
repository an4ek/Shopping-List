# Обновляем libs.versions.toml - добавляем недостающее
cat > gradle/libs.versions.toml << 'TOML'
[versions]
kotlin = "1.9.22"
agp = "8.2.2"
hilt = "2.50"
room = "2.6.1"
lifecycle = "2.7.0"
coroutines = "1.7.3"
ksp = "1.9.22-1.0.17"
konsist = "0.15.1"
junit = "4.13.2"
coreKtx = "1.13.1"
activityCompose = "1.9.0"
composeBom = "2024.02.00"

[libraries]
hilt-android        = { group = "com.google.dagger", name = "hilt-android",           version.ref = "hilt" }
hilt-compiler       = { group = "com.google.dagger", name = "hilt-android-compiler",  version.ref = "hilt" }
room-runtime        = { group = "androidx.room",     name = "room-runtime",            version.ref = "room" }
room-ktx            = { group = "androidx.room",     name = "room-ktx",                version.ref = "room" }
room-compiler       = { group = "androidx.room",     name = "room-compiler",           version.ref = "room" }
lifecycle-viewmodel = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-ktx", version.ref = "lifecycle" }
coroutines-android  = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-android", version.ref = "coroutines" }
coroutines-core     = { group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core",    version.ref = "coroutines" }
konsist             = { group = "com.lemonappdev",   name = "konsist",                 version.ref = "konsist" }
junit               = { group = "junit",             name = "junit",                   version.ref = "junit" }
androidx-core-ktx              = { group = "androidx.core",     name = "core-ktx",                    version.ref = "coreKtx" }
androidx-lifecycle-runtime-ktx = { group = "androidx.lifecycle", name = "lifecycle-runtime-ktx",      version.ref = "lifecycle" }
androidx-activity-compose      = { group = "androidx.activity", name = "activity-compose",            version.ref = "activityCompose" }
androidx-compose-bom            = { group = "androidx.compose",  name = "compose-bom",                version.ref = "composeBom" }
androidx-compose-ui             = { group = "androidx.compose.ui", name = "ui" }
androidx-compose-ui-graphics    = { group = "androidx.compose.ui", name = "ui-graphics" }
androidx-compose-ui-tooling-preview = { group = "androidx.compose.ui", name = "ui-tooling-preview" }
androidx-compose-material3      = { group = "androidx.compose.material3", name = "material3" }
androidx-compose-ui-tooling     = { group = "androidx.compose.ui", name = "ui-tooling" }
androidx-compose-ui-test-manifest = { group = "androidx.compose.ui", name = "ui-test-manifest" }
androidx-compose-ui-test-junit4 = { group = "androidx.compose.ui", name = "ui-test-junit4" }
androidx-junit                  = { group = "androidx.test.ext", name = "junit",                      version = "1.1.5" }
androidx-espresso-core          = { group = "androidx.test.espresso", name = "espresso-core",         version = "3.5.1" }

[plugins]
android-application  = { id = "com.android.application",        version.ref = "agp" }
android-library      = { id = "com.android.library",            version.ref = "agp" }
kotlin-android       = { id = "org.jetbrains.kotlin.android",   version.ref = "kotlin" }
kotlin-jvm           = { id = "org.jetbrains.kotlin.jvm",       version.ref = "kotlin" }
kotlin-compose       = { id = "org.jetbrains.kotlin.plugin.compose", version.ref = "kotlin" }
hilt                 = { id = "com.google.dagger.hilt.android",  version.ref = "hilt" }
ksp                  = { id = "com.google.devtools.ksp",         version.ref = "ksp" }
TOML

# Обновляем app/build.gradle.kts — добавляем hilt и наши модули
cat > app/build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
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
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
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

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
GRADLE

echo "✅ Готово! Теперь нажми Sync Now в Android Studio"
