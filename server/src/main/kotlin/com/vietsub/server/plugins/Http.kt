package com.vietsub.server.plugins

import com.vietsub.server.ServerConfig
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import org.slf4j.event.Level

fun Application.configureHTTP(config: ServerConfig) {
    install(CallLogging) { level = Level.INFO }
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowHeader("X-Telegram-Init-Data")
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Delete)
    }
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            log.error("request failed", cause)
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to (cause.message ?: "internal error")))
        }
    }
}
