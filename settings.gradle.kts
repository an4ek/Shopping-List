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
include(":core-navigation")
include(":core-ui")
include(":feature-list:presentation")
include(":feature-items:presentation")
include(":konsist-tests")
