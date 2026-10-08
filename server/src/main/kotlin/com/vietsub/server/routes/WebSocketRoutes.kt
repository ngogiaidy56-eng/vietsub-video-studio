package com.vietsub.server.routes

import com.vietsub.server.db.VideoJobRepository
import com.vietsub.server.hub.WebSocketHub
import io.ktor.server.routing.Route
import io.ktor.server.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import org.koin.ktor.ext.inject

fun Route.webSocketRoutes() {
    val hub by inject<WebSocketHub>()
    val repo by inject<VideoJobRepository>()
    webSocket("/ws/jobs/{jobId}") {
        val id = call.parameters["jobId"] ?: return@webSocket close()
        repo.find(id)?.let { sendSerialized(it) }
        hub.subscribe(id, this)
        try {
            for (frame in incoming) if (frame is Frame.Close) break
        } catch (_: ClosedReceiveChannelException) {
        } finally {
            hub.unsubscribe(id, this)
        }
    }
}
