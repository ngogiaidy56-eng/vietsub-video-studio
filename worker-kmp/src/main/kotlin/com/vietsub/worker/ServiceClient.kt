package com.vietsub.worker

import com.vietsub.core.model.WorkerJobUpdate
import kotlinx.serialization.json.Json
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class ServiceClient(private val config: WorkerConfig) {
    private val http = HttpClient.newBuilder().build()
    private val json = Json { encodeDefaults = true }

    fun send(update: WorkerJobUpdate) {
        val body = json.encodeToString(WorkerJobUpdate.serializer(), update).toByteArray(StandardCharsets.UTF_8)
        val path = "/api/v1/internal/jobs/${update.jobId}/status"
        val timestamp = System.currentTimeMillis()
        val signature = ServiceAuth.sign(config.serviceId, config.serviceAuthSecret, "POST", path, timestamp, body)
        val request = HttpRequest.newBuilder(URI.create(config.apiBaseUrl + path))
            .header("Content-Type", "application/json")
            .header(ServiceAuth.SERVICE_ID, config.serviceId)
            .header(ServiceAuth.TIMESTAMP, timestamp.toString())
            .header(ServiceAuth.SIGNATURE, signature)
            .POST(HttpRequest.BodyPublishers.ofByteArray(body))
            .build()
        val response = http.send(request, HttpResponse.BodyHandlers.ofString())
        check(response.statusCode() in 200..299) { "API status callback failed: ${response.statusCode()} ${response.body().take(512)}" }
    }
}
