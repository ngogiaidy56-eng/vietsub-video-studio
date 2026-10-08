package com.vietsub.worker

import com.vietsub.core.model.RenderQueueMessage
import com.vietsub.core.model.WorkerJobUpdate
import io.lettuce.core.Consumer
import io.lettuce.core.StreamMessage
import io.lettuce.core.XReadArgs
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.TimeUnit

class FFmpegWorker(private val config: WorkerConfig) {
    private val redis = io.lettuce.core.RedisClient.create(config.redisUrl)
    private val connection: StatefulRedisConnection<String, String> = redis.connect()
    private val commands: RedisCommands<String, String> = connection.sync()
    private val storage = S3Storage(config)
    private val api = ServiceClient(config)
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun runForever() = coroutineScope {
        runCatching {
            commands.xgroupCreate(XReadArgs.StreamOffset.from(config.redisStream, "0-0"), config.redisConsumerGroup, io.lettuce.core.XGroupCreateArgs.Builder.mkstream())
        }
        while (isActive) {
            val entries = withContext(Dispatchers.IO) {
                commands.xreadgroup(
                    Consumer.from(config.redisConsumerGroup, config.workerId),
                    XReadArgs.Builder.count(1).block(java.time.Duration.ofSeconds(5)),
                    XReadArgs.StreamOffset.lastConsumed(config.redisStream)
                )
            }
            entries.forEach { processEntry(it) }
        }
    }

    private suspend fun processEntry(entry: StreamMessage<String, String>) {
        val payload = entry.body["payload"]
        if (payload.isNullOrBlank()) {
            ack(entry)
            return
        }
        val job = runCatching { json.decodeFromString<RenderQueueMessage>(payload) }.getOrElse {
            ack(entry)
            return
        }
        val attempt = job.attempt + 1
        val work = Path.of(config.workDir, job.jobId)
        Files.createDirectories(work)
        val input = work.resolve("input.bin")
        val subtitle = job.subtitleObjectKey?.let { work.resolve("subtitle.srt") }
        val output = work.resolve("output.mp4")
        try {
            send(job, "RUNNING", 2, "Worker accepted job", attempt)
            storage.download(job.inputObjectKey, input)
            if (subtitle != null) storage.download(job.subtitleObjectKey!!, subtitle)
            send(job, "RUNNING", 10, "Assets downloaded", attempt)
            val durationSeconds = probeDurationSeconds(input)
            runFfmpeg(job, input, subtitle, output, attempt, durationSeconds)
            require(Files.exists(output)) { "FFmpeg output missing" }
            require(Files.size(output) <= config.maxOutputBytes) { "FFmpeg output exceeds configured limit" }
            storage.upload(job.outputObjectKey, output, "video/mp4")
            send(job, "COMPLETED", 100, "Render complete", attempt, outputKey = job.outputObjectKey)
            ack(entry)
        } catch (t: Throwable) {
            val message = t.message?.take(1200) ?: t::class.simpleName ?: "unknown error"
            if (attempt < config.maxAttempts) {
                send(job, "RETRYING", 50, "Retry $attempt/${config.maxAttempts}: $message", attempt, error = message)
                withContext(Dispatchers.IO) {
                    commands.xadd(config.redisStream, mapOf("jobId" to job.jobId, "payload" to json.encodeToString(RenderQueueMessage.serializer(), job.copy(attempt = attempt))))
                    commands.xack(config.redisStream, config.redisConsumerGroup, entry.id)
                }
            } else {
                send(job, "FAILED", 100, "Render failed", attempt, error = message)
                ack(entry)
            }
        } finally {
            work.toFile().deleteRecursively()
        }
    }

    private suspend fun runFfmpeg(job: RenderQueueMessage, input: Path, subtitle: Path?, output: Path, attempt: Int, durationSeconds: Double?) = coroutineScope {
        val process = ProcessBuilder(FFmpegCommandBuilder.build(input, subtitle, output, job.style))
            .redirectErrorStream(false)
            .start()
        val progressReader = launch(Dispatchers.IO) {
            process.inputStream.bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parsed = parseProgress(line, durationSeconds)
                    if (parsed != null) send(job, "RUNNING", parsed, "FFmpeg progress", attempt)
                }
            }
        }
        val errorReader = launch(Dispatchers.IO) {
            process.errorStream.bufferedReader().use { it.readText().takeLast(4000) }
        }
        val finished = withContext(Dispatchers.IO) { process.waitFor(config.timeoutSeconds, TimeUnit.SECONDS) }
        if (!finished) {
            process.destroyForcibly()
            throw IllegalStateException("FFmpeg timeout after ${config.timeoutSeconds}s")
        }
        progressReader.join()
        errorReader.join()
        if (process.exitValue() != 0) throw IllegalStateException("ffmpeg exit=${process.exitValue()}")
    }

    private fun parseProgress(line: String, durationSeconds: Double?): Int? {
        if (!line.startsWith("out_time_ms=")) return null
        val micros = line.substringAfter('=').toLongOrNull() ?: return null
        val seconds = micros / 1_000_000.0
        return if (durationSeconds != null && durationSeconds > 0.0) {
            (seconds / durationSeconds * 95.0).toInt().coerceIn(1, 95)
        } else {
            (seconds.toInt()).coerceIn(1, 95)
        }
    }

    private fun probeDurationSeconds(input: Path): Double? {
        return runCatching {
            val process = ProcessBuilder(
                "ffprobe", "-v", "error", "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1", input.toString()
            ).redirectErrorStream(true).start()
            val value = process.inputStream.bufferedReader().use { it.readText().trim().toDoubleOrNull() }
            process.waitFor(20, TimeUnit.SECONDS)
            value
        }.getOrNull()
    }

    private fun send(job: RenderQueueMessage, state: String, progress: Int, message: String, attempt: Int, outputKey: String? = null, error: String? = null) {
        api.send(WorkerJobUpdate(job.jobId, state, progress, message, attempt, config.workerId, outputKey, error))
    }

    private suspend fun ack(entry: StreamMessage<String, String>) = withContext(Dispatchers.IO) {
        commands.xack(config.redisStream, config.redisConsumerGroup, entry.id)
    }

    fun close() { connection.close(); redis.shutdown() }
}
