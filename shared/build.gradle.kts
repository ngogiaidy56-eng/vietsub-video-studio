plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.androidKmpLibrary)
}

kotlin {
    jvm()
    androidLibrary {
        namespace = "com.vietsub.shared"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()
    macosX64()
    macosArm64()
    js(IR) { browser() }
    wasmJs { browser() }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.coroutines.core)
            implementation(libs.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.neg)
            implementation(libs.ktor.client.logging)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.neg)
        }
        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}
