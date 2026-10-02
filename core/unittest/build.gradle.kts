import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "fr.leboncoin.androidrecruitmenttestapp.core.unittest"
    compileSdk = 36 // with build-logic we can remove this line

    defaultConfig {
        minSdk = 24
    }
    buildFeatures { buildConfig = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    // Test
    implementation(libs.junit)
    implementation(libs.kotlinx.coroutines.test)
}
