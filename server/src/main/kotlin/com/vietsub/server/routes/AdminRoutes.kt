package com.vietsub.server.routes
import io.ktor.server.routing.Route
import com.vietsub.server.ServerConfig
fun Route.adminRoutes(config: ServerConfig) = apiRoutes(config)
