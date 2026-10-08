package com.vietsub.server.services

import com.vietsub.core.model.ServerStatus
import com.vietsub.server.ServerConfig
import com.vietsub.server.db.VideoJobRepository
import com.vietsub.server.queue.RedisQueueService
import com.vietsub.server.storage.ObjectStorageService
import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

class SystemMonitorService(
    private val config: ServerConfig,
    private val repository: VideoJobRepository,
    private val queue: RedisQueueService,
    private val storage: ObjectStorageService
) {
    private val started = System.nanoTime()

    suspend fun status(): ServerStatus {
        val runtime = Runtime.getRuntime()
        val os = ManagementFactory.getOperatingSystemMXBean() as? com.sun.management.OperatingSystemMXBean
        val cpu = ((os?.cpuLoad ?: 0.0) * 100).coerceIn(0.0, 100.0)
        val queueSize = runCatching { queue.queueSize() }.getOrDefault(-1)
        val activeJobs = runCatching { repository.countActive() }.getOrDefault(-1)
        val redisOk = queueSize >= 0
        val storageOk = runCatching { storage.healthy() }.getOrDefault(false)
        return ServerStatus(
            status = if (redisOk && storageOk) "online" else "degraded",
            version = "1.1.0-production-core",
            uptimeMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - started),
            cpuPercent = cpu,
            memoryUsedMb = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024,
            memoryMaxMb = runtime.maxMemory() / 1024 / 1024,
            queueSize = queueSize,
            activeJobs = activeJobs,
            maintenance = config.maintenance,
            redisOk = redisOk,
            storageOk = storageOk
        )
    }
}
