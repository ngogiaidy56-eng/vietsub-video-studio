plugins {
    application
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
}

application { mainClass.set("com.vietsub.worker.WorkerApplicationKt") }
kotlin { jvmToolchain(17) }

dependencies {
    implementation(project(":shared"))
    implementation(libs.coroutines.core)
    implementation(libs.serialization.json)
    implementation(libs.lettuce)
    implementation(libs.aws.s3)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.neg)
    implementation(libs.logback)
}
