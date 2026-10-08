package com.vietsub.server.services

import com.vietsub.core.model.JobStatus
import com.vietsub.core.model.RenderQueueMessage
import com.vietsub.core.model.RenderRequest
import com.vietsub.core.model.SubtitleStyle
import com.vietsub.core.model.WorkerJobUpdate
import com.vietsub.server.ServerConfig
import com.vietsub.server.db.VideoJobRepository
import com.vietsub.server.hub.WebSocketHub
import com.vietsub.server.queue.RedisQueueService
import com.vietsub.server.storage.ObjectStorageService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class RenderJobService(
    private val config: ServerConfig,
    private val repository: VideoJobRepository,
    private val queue: RedisQueueService,
    private val storage: ObjectStorageService,
    private val hub: WebSocketHub
) {
    suspend fun submit(request: RenderRequest): JobStatus {
        validateObjectKey(request.inputObjectKey)
        request.subtitleObjectKey?.let(::validateObjectKey)
        val jobId = request.jobId ?: UUID.randomUUID().toString()
        request.outputObjectKey?.let(::validateObjectKey)
        val outputKey = request.outputObjectKey ?: "outputs/$jobId.mp4"
        val existing = request.idempotencyKey?.let { repository.findByIdempotencyKey(it) }
        if (existing != null) return existing

        val style = SubtitleStyle(
            fontFamily = request.fontFamily.take(64),
            fontSize = request.fontSize.coerceIn(8, 160),
            bold = request.bold,
            italic = request.italic,
            primaryColor = request.primaryColor,
            outlineColor = request.outlineColor,
            outlineWidth = request.outlineWidth.coerceIn(0, 12),
            alignment = request.alignment.coerceIn(1, 9),
            marginV = request.marginV.coerceIn(0, 500)
        )
        val queued = JobStatus(
            jobId = jobId,
            state = "QUEUED",
            progress = 0,
            message = "Queued",
            inputObjectKey = request.inputObjectKey,
            subtitleObjectKey = request.subtitleObjectKey,
            outputObjectKey = outputKey,
            attempt = 0
        )
        repository.createIfAbsent(queued, request.idempotencyKey)
        hub.publish(queued)
        withContext(Dispatchers.IO) {
            queue.enqueue(RenderQueueMessage(jobId, request.inputObjectKey, request.subtitleObjectKey, outputKey, 0, style))
        }
        return queued
    }

    suspend fun applyWorkerUpdate(update: WorkerJobUpdate): JobStatus {
        val old = repository.find(update.jobId) ?: error("Job not found: ${update.jobId}")
        val state = if (update.state == "COMPLETED" && update.outputObjectKey != null) "COMPLETED" else update.state
        val status = old.copy(
            state = state,
            progress = update.progress.coerceIn(0, 100),
            message = update.message.take(2048),
            error = update.error,
            outputObjectKey = update.outputObjectKey ?: old.outputObjectKey,
            attempt = update.attempt,
            workerId = update.workerId
        )
        repository.update(status)
        val publicStatus = if (status.state == "COMPLETED" && status.outputObjectKey != null) {
            val url = storage.presignDownload(status.outputObjectKey, config.presignTtlSeconds).downloadUrl
            status.copy(outputUrl = url)
        } else status
        hub.publish(publicStatus)
        return publicStatus
    }

    private fun validateObjectKey(key: String) {
        require(key.isNotBlank() && key.length <= 2048) { "Invalid object key" }
        require(!key.contains("..")) { "Parent traversal is not allowed" }
        require(key.all { it.isLetterOrDigit() || it in setOf('/', '.', '-', '_', ':') }) { "Object key contains unsupported characters" }
    }
}
