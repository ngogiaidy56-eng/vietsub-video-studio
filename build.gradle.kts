plugins {
    id("org.jetbrains.kotlin.multiplatform") version libs.versions.kotlin.get() apply false
    id("org.jetbrains.kotlin.plugin.serialization") version libs.versions.kotlin.get() apply false
    id("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin.get() apply false
    id("com.android.application") version libs.versions.androidGradlePlugin.get() apply false
    alias(libs.plugins.androidKmpLibrary) apply false
    id("org.jetbrains.compose") version libs.versions.compose.get() apply false
    id("io.ktor.plugin") version libs.versions.ktor.get() apply false
}

allprojects {
    group = "com.vietsub"
    version = "3.0.0"
}
