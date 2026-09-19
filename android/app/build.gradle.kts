import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
}

val keystorePropertiesFile = rootProject.layout.projectDirectory.file("keystore.properties").asFile
val keystoreProperties = Properties()
val hasReleaseKeystore = keystorePropertiesFile.isFile.also { exists ->
    if (exists) {
        keystorePropertiesFile.inputStream().use { keystoreProperties.load(it) }
    }
}

android {
    namespace = "com.notesdusecouriste.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.notesdusecouriste.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "0.3.0-beta"
    }

    signingConfigs {
        if (hasReleaseKeystore) {
            create("release") {
                keyAlias = keystoreProperties.getProperty("keyAlias")?.trim()
                keyPassword = keystoreProperties.getProperty("keyPassword")?.trim()
                storePassword = keystoreProperties.getProperty("storePassword")?.trim()
                val storeFileName = keystoreProperties.getProperty("storeFile")?.trim()
                    ?: error("keystore.properties : propriété storeFile manquante")
                storeFile = rootProject.layout.projectDirectory.file(storeFileName).asFile
            }
        }
    }

    packaging {
        jniLibs {
            // Conserver les symboles des .so tiers (Compose, DataStore) pour la Play Console.
            keepDebugSymbols += "**/*.so"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
            if (hasReleaseKeystore) {
                signingConfig = signingConfigs.getByName("release")
            }
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

// ZIP pour upload manuel « Symboles de débogage natifs » (Play Console → App bundle explorer → Assets).
val packageReleaseNativeDebugSymbols = tasks.register<Zip>("packageReleaseNativeDebugSymbols") {
    archiveFileName.set("native-debug-symbols.zip")
    destinationDirectory.set(layout.buildDirectory.dir("outputs/native-debug-symbols/release"))
    val mergeLibs = layout.buildDirectory.dir(
        "intermediates/merged_native_libs/release/mergeReleaseNativeLibs/out/lib",
    )
    // Racine du ZIP = ABI (arm64-v8a, armeabi-v7a, …) — pas de dossier « lib ».
    from(mergeLibs) {
        include("**/*.so")
    }
    onlyIf { mergeLibs.get().asFile.exists() }
}

afterEvaluate {
    tasks.matching { it.name == "bundleRelease" }.configureEach {
        finalizedBy(packageReleaseNativeDebugSymbols)
    }
}

dependencies {
    implementation(project(":core:core-data"))
    implementation(project(":core:core-ui"))
    implementation(project(":feature:feature-intervention-notes"))
    implementation(project(":feature:feature-aide-memoire"))
    implementation(project(":feature:feature-onboarding"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
