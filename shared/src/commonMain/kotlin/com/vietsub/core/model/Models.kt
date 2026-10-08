package com.vietsub.core.model

import kotlinx.serialization.Serializable

@Serializable
data class SubtitleItem(
    val id: String,
    val startMs: Long,
    val endMs: Long,
    val text: String,
    val translatedText: String? = null,
    val style: SubtitleStyle = SubtitleStyle()
) {
    init {
        require(startMs >= 0) { "startMs must be >= 0" }
        require(endMs > startMs) { "endMs must be > startMs" }
    }
}

@Serializable
data class SubtitleStyle(
    val fontFamily: String = "Arial",
    val fontSize: Int = 34,
    val bold: Boolean = true,
    val italic: Boolean = false,
    val primaryColor: String = "&H00FFFFFF",
    val outlineColor: String = "&H00000000",
    val outlineWidth: Int = 2,
    val shadow: Int = 0,
    val alignment: Int = 2,
    val marginV: Int = 42
)

@Serializable
data class VideoMeta(
    val fileName: String,
    val mimeType: String,
    val sizeBytes: Long,
    val durationMs: Long? = null,
    val width: Int? = null,
    val height: Int? = null,
    val fps: Double? = null
)

@Serializable
data class ServerStatus(
    val status: String,
    val version: String,
    val uptimeMs: Long,
    val cpuPercent: Double,
    val memoryUsedMb: Long,
    val memoryMaxMb: Long,
    val queueSize: Int,
    val activeJobs: Int,
    val maintenance: Boolean,
    val redisOk: Boolean = false,
    val storageOk: Boolean = false
)

@Serializable
data class FeatureFlagConfig(
    val enableTranslation: Boolean = true,
    val enableTranscription: Boolean = true,
    val enableRender: Boolean = true,
    val enableTelegram: Boolean = true,
    val enableAdmin: Boolean = true,
    val enableObjectStorage: Boolean = true,
    val enableRenderQueue: Boolean = true
)

@Serializable
data class TelegramUser(
    val id: Long,
    val firstName: String? = null,
    val lastName: String? = null,
    val username: String? = null
)

@Serializable
data class RenderRequest(
    val jobId: String? = null,
    val idempotencyKey: String? = null,
    val inputObjectKey: String,
    val subtitleObjectKey: String? = null,
    val outputObjectKey: String? = null,
    val fontFamily: String = "Arial",
    val fontSize: Int = 34,
    val bold: Boolean = true,
    val italic: Boolean = false,
    val primaryColor: String = "&H00FFFFFF",
    val outlineColor: String = "&H00000000",
    val outlineWidth: Int = 2,
    val alignment: Int = 2,
    val marginV: Int = 42
)

@Serializable
data class RenderQueueMessage(
    val jobId: String,
    val inputObjectKey: String,
    val subtitleObjectKey: String? = null,
    val outputObjectKey: String,
    val attempt: Int,
    val style: SubtitleStyle = SubtitleStyle()
)

@Serializable
data class WorkerJobUpdate(
    val jobId: String,
    val state: String,
    val progress: Int,
    val message: String,
    val attempt: Int,
    val workerId: String,
    val outputObjectKey: String? = null,
    val error: String? = null
)

@Serializable
data class JobStatus(
    val jobId: String,
    val state: String,
    val progress: Int,
    val message: String,
    val outputUrl: String? = null,
    val error: String? = null,
    val inputObjectKey: String? = null,
    val subtitleObjectKey: String? = null,
    val outputObjectKey: String? = null,
    val attempt: Int = 0,
    val workerId: String? = null
)

@Serializable
data class UploadPresignRequest(
    val fileName: String,
    val contentType: String = "application/octet-stream"
)

@Serializable
data class UploadPresignResponse(
    val objectKey: String,
    val uploadUrl: String,
    val expiresInSeconds: Long
)

@Serializable
data class ApiEnvelope<T>(val data: T)

@Serializable
data class TranslateRequest(val srt: String, val targetLanguage: String = "Vietnamese")

@Serializable
data class TranslateResponse(val srt: String)
