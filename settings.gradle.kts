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
include(":feature-list")
include(":feature-items")
include(":feature-category")
include(":feature-history")
include(":konsist-tests")
