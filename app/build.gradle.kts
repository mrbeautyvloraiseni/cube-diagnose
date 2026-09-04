plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "ch.mrbeauty.cubediagnose"
    compileSdk = 35

    defaultConfig {
        applicationId = "ch.mrbeauty.cubediagnose"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}
