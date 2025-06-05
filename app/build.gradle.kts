plugins {
    alias(libs.plugins.application)
    alias(libs.plugins.kotlin)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.example.water_tracker"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.water_tracker"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            enableUnitTestCoverage = true
            isDebuggable = true
            isMinifyEnabled = false
        }

        release {
            isMinifyEnabled = true
            isDebuggable = false
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        compose = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            all {
                configure<JacocoTaskExtension> {
                    isIncludeNoLocationClasses = true
                }
            }
        }
    }
}

dependencies {
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)

    // Compose
    implementation("androidx.compose.material:material-icons-extended:1.7.7")
    implementation(libs.compose.runtime)
    implementation(libs.compose.material)
    implementation(libs.compose.ui)
    implementation(libs.androidx.foundation.android)
    implementation(libs.androidx.media3.common.ktx)
    debugImplementation(libs.compose.uitooling)
    implementation(libs.compose.themeadapter)
    implementation(libs.compose.activity)
    implementation(libs.compose.navigation)

    implementation(libs.systemuicontroller)
    implementation(libs.splashscreen)

    // Room Database
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Hilt
    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.hilt.work)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)

    // Work manager
    implementation(libs.workmanager)
}
