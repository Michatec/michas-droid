plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.michatec.store"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.michatec.store"
        minSdk = 30
        targetSdk = 37
        versionCode = 18
        versionName = "1.8"

        val languages = listOf("en", "de")
        buildConfigField("String[]", "LANGUAGES", "{ \"${languages.joinToString("\", \"")}\" }")

        @Suppress("UnstableApiUsage")
        androidResources {
            localeFilters += languages
        }
    }

    buildFeatures {
        buildConfig = true
    }

    sourceSets {
        getByName("main") {
            kotlin.directories += "src/main/kotlin"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    flavorDimensions += "distributor"
    productFlavors {
        create("michas") {
            dimension = "distributor"
            buildConfigField("boolean", "FLAVOR_FDROID", "false")
        }
        create("fdroid") {
            dimension = "distributor"
            buildConfigField("boolean", "FLAVOR_FDROID", "true")
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard.pro"
            )
        }
    }
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.core.ktx)
    implementation(libs.fragment.ktx)
    implementation(libs.viewpager2)
    implementation(libs.vectordrawable)
    implementation(libs.okhttp)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.jackson.core)
    implementation(libs.picasso)
    implementation(libs.freedroidwarn)
}