plugins {
    application
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("io.ktor.plugin")
}

application { mainClass.set("com.vietsub.server.ApplicationKt") }
kotlin { jvmToolchain(17) }

dependencies {
    implementation(project(":shared"))
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.neg)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.websockets)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.status)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.neg)
    implementation(libs.serialization.json)
    implementation(libs.coroutines.core)
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.exposed.core)
    implementation(libs.exposed.jdbc)
    implementation(libs.hikari)
    implementation(libs.h2)
    implementation(libs.postgres)
    implementation(libs.lettuce)
    implementation(libs.aws.s3)
    implementation(libs.aws.s3.presigner)
    implementation(libs.logback)
    testImplementation(libs.ktor.server.test)
    testImplementation(libs.kotlin.test)
}
