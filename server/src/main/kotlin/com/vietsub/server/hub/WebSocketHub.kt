package com.vietsub.server.hub

import com.vietsub.core.model.JobStatus
import io.ktor.server.websocket.*
import java.util.concurrent.ConcurrentHashMap

class WebSocketHub {
    private val sessions = ConcurrentHashMap<String, MutableSet<DefaultWebSocketServerSession>>()

    suspend fun subscribe(jobId: String, session: DefaultWebSocketServerSession) {
        sessions.computeIfAbsent(jobId) { ConcurrentHashMap.newKeySet() }.add(session)
    }

    fun unsubscribe(jobId: String, session: DefaultWebSocketServerSession) { sessions[jobId]?.remove(session) }

    suspend fun publish(status: JobStatus) {
        sessions[status.jobId]?.toList()?.forEach { session ->
            try { session.sendSerialized(status) } catch (_: Throwable) { sessions[status.jobId]?.remove(session) }
        }
    }
}
