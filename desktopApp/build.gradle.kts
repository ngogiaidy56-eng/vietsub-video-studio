plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

kotlin {
    jvm()
    sourceSets.jvmMain.dependencies {
        implementation(compose.desktop.currentOs)
        implementation(project(":composeApp"))
    }
}

compose.desktop {
    application {
        mainClass = "com.vietsub.desktop.MainKt"
        nativeDistributions {
            targetFormats(
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg,
                org.jetbrains.compose.desktop.application.dsl.TargetFormat.Deb
            )
            packageName = "VietsubVideoStudio"
            packageVersion = "1.0.0"
        }
    }
}
