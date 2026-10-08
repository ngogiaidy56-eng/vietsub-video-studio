package com.vietsub.server.queue

import com.vietsub.core.model.RenderQueueMessage
import com.vietsub.server.ServerConfig
import io.lettuce.core.RedisClient
import io.lettuce.core.XGroupCreateArgs
import io.lettuce.core.XReadArgs
import io.lettuce.core.api.sync.RedisCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

class RedisQueueService(private val config: ServerConfig) : AutoCloseable {
    private val client = RedisClient.create(config.redisUrl)
    private val connection = client.connect()
    private val commands: RedisCommands<String, String> = connection.sync()
    private val json = Json { ignoreUnknownKeys = true }

    init {
        runCatching {
            commands.xgroupCreate(XReadArgs.StreamOffset.from(config.redisStream, "0-0"), config.redisConsumerGroup, XGroupCreateArgs.Builder.mkstream())
        }
    }

    suspend fun enqueue(message: RenderQueueMessage) = withContext(Dispatchers.IO) {
        commands.xadd(config.redisStream, mapOf(
            "jobId" to message.jobId,
            "payload" to json.encodeToString(RenderQueueMessage.serializer(), message)
        ))
    }

    suspend fun queueSize(): Int = withContext(Dispatchers.IO) { commands.xlen(config.redisStream).toInt() }

    override fun close() {
        connection.close()
        client.shutdown()
    }
}
