package com.vietsub.server.plugins

import com.vietsub.server.ServerConfig
import com.vietsub.server.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.respondText
import io.ktor.server.routing.*

fun Application.configureRouting(config: ServerConfig) {
    routing {
        get("/") { call.respondText("Vietsub Video Studio API") }
        apiRoutes(config)
        telegramRoutes(config)
        webSocketRoutes()
    }
}
