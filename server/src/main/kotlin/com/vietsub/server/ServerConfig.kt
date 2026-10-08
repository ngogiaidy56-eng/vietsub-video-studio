package com.vietsub.server

import java.nio.file.Path
import kotlin.io.path.Path

data class ServerConfig(
    val host: String,
    val port: Int,
    val publicBaseUrl: String,
    val dataDir: Path,
    val databaseUrl: String,
    val databaseUser: String,
    val databasePassword: String,
    val databasePoolSize: Int,
    val openAiApiKey: String,
    val geminiApiKey: String,
    val geminiModel: String,
    val telegramBotToken: String,
    val telegramAdminIds: Set<Long>,
    val telegramWebhookSecret: String,
    val maintenance: Boolean,
    val redisUrl: String,
    val redisStream: String,
    val redisConsumerGroup: String,
    val storageDriver: String,
    val storageBucket: String,
    val storageRegion: String,
    val storageEndpoint: String,
    val storagePresignEndpoint: String,
    val storageAccessKey: String,
    val storageSecretKey: String,
    val storagePublicBaseUrl: String,
    val presignTtlSeconds: Long,
    val serviceId: String,
    val serviceAuthSecret: String,
    val workerMaxAttempts: Int
) {
    companion object {
        fun fromEnvironment() = ServerConfig(
            host = env("HOST", "0.0.0.0"),
            port = env("PORT", "8080").toInt(),
            publicBaseUrl = env("PUBLIC_BASE_URL", "http://localhost:8080").trimEnd('/'),
            dataDir = Path(env("DATA_DIR", "./data")),
            databaseUrl = env("DATABASE_URL", "jdbc:h2:file:./data/vietsub"),
            databaseUser = env("DATABASE_USER", "sa"),
            databasePassword = env("DATABASE_PASSWORD", ""),
            databasePoolSize = env("DATABASE_POOL_SIZE", "10").toInt(),
            openAiApiKey = env("OPENAI_API_KEY", ""),
            geminiApiKey = env("GEMINI_API_KEY", ""),
            geminiModel = env("GEMINI_MODEL", "gemini-2.5-flash"),
            telegramBotToken = env("TELEGRAM_BOT_TOKEN", ""),
            telegramAdminIds = env("TELEGRAM_ADMIN_IDS", "").split(',').mapNotNull { it.trim().toLongOrNull() }.toSet(),
            telegramWebhookSecret = env("TELEGRAM_WEBHOOK_SECRET", ""),
            maintenance = env("MAINTENANCE", "false").toBoolean(),
            redisUrl = env("REDIS_URL", "redis://localhost:6379"),
            redisStream = env("REDIS_RENDER_STREAM", "vietsub:render"),
            redisConsumerGroup = env("REDIS_RENDER_GROUP", "vietsub-workers"),
            storageDriver = env("STORAGE_DRIVER", "s3").lowercase(),
            storageBucket = env("STORAGE_BUCKET", "vietsub"),
            storageRegion = env("STORAGE_REGION", "auto"),
            storageEndpoint = env("STORAGE_ENDPOINT", ""),
            storagePresignEndpoint = env("STORAGE_PRESIGN_ENDPOINT", env("STORAGE_ENDPOINT", "")),
            storageAccessKey = env("STORAGE_ACCESS_KEY", ""),
            storageSecretKey = env("STORAGE_SECRET_KEY", ""),
            storagePublicBaseUrl = env("STORAGE_PUBLIC_BASE_URL", "").trimEnd('/'),
            presignTtlSeconds = env("PRESIGN_TTL_SECONDS", "1800").toLong(),
            serviceId = env("SERVICE_ID", "vietsub-api"),
            serviceAuthSecret = env("SERVICE_AUTH_SECRET", ""),
            workerMaxAttempts = env("WORKER_MAX_ATTEMPTS", "3").toInt()
        )
        private fun env(name: String, default: String) = System.getenv(name)?.takeIf { it.isNotBlank() } ?: default
    }
}
