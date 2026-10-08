plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
    id("org.jetbrains.kotlin.plugin.serialization")
    alias(libs.plugins.androidKmpLibrary)
}

kotlin {
    jvm()
    androidLibrary {
        namespace = "com.vietsub.composeapp"
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
    }
    iosX64(); iosArm64(); iosSimulatorArm64(); macosX64(); macosArm64()
    js(IR) { browser(); binaries.executable() }
    wasmJs { browser(); binaries.executable() }
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.coroutines.core)
            implementation(libs.serialization.json)
            implementation(project(":shared"))
        }
        jvmMain.dependencies { implementation(compose.desktop.currentOs) }
        androidMain.dependencies { implementation(libs.exoplayer); implementation(libs.exoplayer.ui) }
    }
}
compose.resources { packageOfResClass = "com.vietsub.app.resources" }
