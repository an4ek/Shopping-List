# Корневой build.gradle.kts
cat > build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}
GRADLE

# domain/build.gradle.kts
cat > domain/build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(libs.coroutines.core)
}
GRADLE

# core/build.gradle.kts
cat > core/build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.core"
    compileSdk = 34
    defaultConfig { minSdk = 24 }
}

dependencies {
    implementation(libs.coroutines.android)
    implementation(libs.lifecycle.viewmodel)
}
GRADLE

# data/build.gradle.kts
cat > data/build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.data"
    compileSdk = 34
    defaultConfig { minSdk = 24 }
}

dependencies {
    implementation(project(":domain"))
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.coroutines.android)
}
GRADLE

# konsist-tests/build.gradle.kts
cat > konsist-tests/build.gradle.kts << 'GRADLE'
plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    testImplementation(libs.konsist)
    testImplementation(libs.junit)
}
GRADLE

# gradle/libs.versions.toml
mkdir -p gradle
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

[plugins]
android-application = { id = "com.android.application",       version.ref = "agp" }
android-library     = { id = "com.android.library",           version.ref = "agp" }
kotlin-android      = { id = "org.jetbrains.kotlin.android",  version.ref = "kotlin" }
kotlin-jvm          = { id = "org.jetbrains.kotlin.jvm",      version.ref = "kotlin" }
hilt                = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }
ksp                 = { id = "com.google.devtools.ksp",        version.ref = "ksp" }
TOML

echo "✅ Все build.gradle.kts и libs.versions.toml обновлены!"
