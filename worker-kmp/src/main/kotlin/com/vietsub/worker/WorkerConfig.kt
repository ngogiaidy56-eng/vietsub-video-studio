package com.vietsub.worker

data class WorkerConfig(
    val workerId: String,
    val redisUrl: String,
    val redisStream: String,
    val redisConsumerGroup: String,
    val apiBaseUrl: String,
    val serviceId: String,
    val serviceAuthSecret: String,
    val storageBucket: String,
    val storageRegion: String,
    val storageEndpoint: String,
    val storageAccessKey: String,
    val storageSecretKey: String,
    val workDir: String,
    val maxAttempts: Int,
    val timeoutSeconds: Long,
    val maxOutputBytes: Long
) {
    companion object {
        fun fromEnvironment() = WorkerConfig(
            workerId = env("WORKER_ID", "worker-${java.util.UUID.randomUUID()}"),
            redisUrl = env("REDIS_URL", "redis://localhost:6379"),
            redisStream = env("REDIS_RENDER_STREAM", "vietsub:render"),
            redisConsumerGroup = env("REDIS_RENDER_GROUP", "vietsub-workers"),
            apiBaseUrl = env("API_BASE_URL", "http://localhost:8080").trimEnd('/'),
            serviceId = env("SERVICE_ID", "ffmpeg-worker"),
            serviceAuthSecret = env("SERVICE_AUTH_SECRET", ""),
            storageBucket = env("STORAGE_BUCKET", "vietsub"),
            storageRegion = env("STORAGE_REGION", "auto"),
            storageEndpoint = env("STORAGE_ENDPOINT", ""),
            storageAccessKey = env("STORAGE_ACCESS_KEY", ""),
            storageSecretKey = env("STORAGE_SECRET_KEY", ""),
            workDir = env("WORK_DIR", "/work"),
            maxAttempts = env("WORKER_MAX_ATTEMPTS", "3").toInt(),
            timeoutSeconds = env("FFMPEG_TIMEOUT_SECONDS", "3600").toLong(),
            maxOutputBytes = env("FFMPEG_MAX_OUTPUT_BYTES", "10737418240").toLong()
        )
        private fun env(name: String, default: String) = System.getenv(name)?.takeIf { it.isNotBlank() } ?: default
    }
}
