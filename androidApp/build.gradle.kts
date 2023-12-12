import com.android.build.api.dsl.Packaging

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.googleServices)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kapt)
}

android {
    namespace = "com.mahkxai.gactour.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.mahkxai.gactour.android"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            pickFirsts += "META-INF/gradle/incremental.annotation.processors"
        }
        jniLibs {
            pickFirsts += "lib/armeabi-v7a/libc++_shared.so"
            pickFirsts += "lib/arm64-v8a/libc++_shared.so"
            pickFirsts += "lib/x86/libc++_shared.so"
            pickFirsts += "lib/x86_64/libc++_shared.so"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles (
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material3)
    implementation(libs.lifecycle.viewmodel)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.google.fonts)

    // Dagger Hilt
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    // Mapbox
    implementation(libs.mapbox)
    implementation(libs.mapbox.compose)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.geofire)
    implementation(libs.firebase.auth.ktx)

    // Location
    implementation(libs.play.services)

    // Accompanist Permission
    implementation(libs.accompanist.permission)

    // Compose Destinations
    implementation(libs.compose.destinations.animations.core)
    ksp(libs.compose.destinations.ksp)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // Media3 ExoPlayer
    implementation(libs.exoplayer)
    implementation(libs.exoplayer.ui)
    implementation(libs.ffmpeg.kit)

    // CameraX
    implementation(libs.camerax.core)
    implementation(libs.camerax.camera2)
    implementation(libs.camerax.lifecycle)
    implementation(libs.camerax.video)
    implementation(libs.camerax.view)
    implementation(libs.camerax.extensions)

    debugImplementation(libs.compose.ui.tooling)
}