# Обновляем settings.gradle.kts
cat > settings.gradle.kts << 'GRADLE'
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ShoppingListApp"
include(":app")
include(":core")
include(":domain")
include(":data")
include(":konsist-tests")
GRADLE

echo "✅ settings.gradle.kts обновлён!"
