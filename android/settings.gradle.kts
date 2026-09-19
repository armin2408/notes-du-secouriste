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

rootProject.name = "NotesDuSecouriste"

include(":app")
include(":core:core-data")
include(":core:core-ui")
include(":feature:feature-intervention-notes")
include(":feature:feature-aide-memoire")
include(":feature:feature-onboarding")
