enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()

        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
            authentication {
                create<BasicAuthentication>("basic")
            }
            credentials {
                username = "mapbox"  // Do not change this.
                password = providers.gradleProperty("MAPBOX_DOWNLOADS_TOKEN").get()
            }
        }

    }
}

rootProject.name = "GACTour"
include(":androidApp")
include(":shared")