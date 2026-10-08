package com.vietsub.server.routes

import com.vietsub.core.model.*
import com.vietsub.server.ServerConfig
import com.vietsub.server.db.VideoJobRepository
import com.vietsub.server.plugins.requireServiceAuth
import com.vietsub.server.services.*
import com.vietsub.server.storage.ObjectStorageService
import io.ktor.http.*
import io.ktor.http.content.PartData
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.util.UUID

fun Route.apiRoutes(config: ServerConfig) {
    val repo by inject<VideoJobRepository>()
    val ai by inject<AiService>()
    val whisper by inject<WhisperService>()
    val monitor by inject<SystemMonitorService>()
    val flags by inject<FeatureFlagService>()
    val storage by inject<ObjectStorageService>()
    val jobs by inject<RenderJobService>()
    val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    route("/api/v1") {
        get("/health") { call.respond(mapOf("status" to "ok")) }
        get("/ready") {
            val s = monitor.status()
            if (s.redisOk && s.storageOk) call.respond(s) else call.respond(HttpStatusCode.ServiceUnavailable, s)
        }
        get("/status") { call.respond(monitor.status()) }
        get("/features") { call.respond(flags.current()) }

        get("/jobs/{jobId}") {
            val jobId = call.parameters["jobId"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val job = repo.find(jobId) ?: return@get call.respond(HttpStatusCode.NotFound)
            val out = if (job.state == "COMPLETED" && job.outputObjectKey != null) {
                storage.presignDownload(job.outputObjectKey, config.presignTtlSeconds).downloadUrl
            } else null
            call.respond(job.copy(outputUrl = out))
        }

        post("/uploads/presign") {
            val request = call.receive<UploadPresignRequest>()
            val safeName = request.fileName.substringAfterLast('/').substringAfterLast('\\').take(120)
            val key = "uploads/${UUID.randomUUID()}-$safeName"
            val result = storage.presignUpload(key, request.contentType, config.presignTtlSeconds)
            call.respond(UploadPresignResponse(result.objectKey, result.uploadUrl, result.expiresInSeconds))
        }

        post("/uploads") {
            val multipart = call.receiveMultipart()
            var objectKey: String? = null
            multipart.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        val name = part.originalFileName?.substringAfterLast('/')?.substringAfterLast('\\')?.take(120) ?: "upload.bin"
                        val key = "uploads/${UUID.randomUUID()}-$name"
                        storage.put(key, part.streamProvider().readBytes(), part.contentType?.toString() ?: ContentType.Application.OctetStream.toString())
                        objectKey = key
                    }
                    else -> Unit
                }
                part.dispose()
            }
            if (objectKey == null) return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "file is required"))
            call.respond(mapOf("objectKey" to objectKey))
        }

        post("/subtitles/translate") {
            val request = call.receive<TranslateRequest>()
            call.respond(TranslateResponse(ai.translateSrt(request.srt, request.targetLanguage)))
        }

        post("/subtitles/transcribe") {
            val multipart = call.receiveMultipart()
            var uploaded: java.nio.file.Path? = null
            multipart.forEachPart { part ->
                if (part is PartData.FileItem) {
                    val target = config.dataDir.resolve("uploads").resolve("${UUID.randomUUID()}-${part.originalFileName ?: "input.bin"}")
                    target.parent.toFile().mkdirs()
                    target.toFile().writeBytes(part.streamProvider().readBytes())
                    uploaded = target
                }
                part.dispose()
            }
            val file = uploaded ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "file is required"))
            call.respond(mapOf("srt" to whisper.transcribe(file)))
        }

        post("/render") {
            call.respond(jobs.submit(call.receive()))
        }

        post("/internal/jobs/{jobId}/status") {
            val raw = call.receiveText()
            if (!call.requireServiceAuth(config, raw.toByteArray())) return@post
            val update = json.decodeFromString<WorkerJobUpdate>(raw)
            val expectedJob = call.parameters["jobId"]
            if (expectedJob != update.jobId) return@post call.respond(HttpStatusCode.BadRequest)
            call.respond(jobs.applyWorkerUpdate(update))
        }

        delete("/jobs/{jobId}") {
            val jobId = call.parameters["jobId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
            val current = repo.find(jobId) ?: return@delete call.respond(HttpStatusCode.NotFound)
            val cancelled = current.copy(state = "CANCELLED", progress = current.progress, message = "Cancelled")
            repo.update(cancelled)
            call.respond(cancelled)
        }
    }
}
