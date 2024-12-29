pluginManagement {
  repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
    
    repositories {
    maven(url = "https://jitpack.io")
}
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()
    maven(url = "https://jitpack.io")
  }
}

rootProject.name = "Scene Editor"

include(":app")
Include(":feature:scene")