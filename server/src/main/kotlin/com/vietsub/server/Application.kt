package com.vietsub.server

import com.vietsub.server.db.DatabaseFactory
import com.vietsub.server.hub.WebSocketHub
import com.vietsub.server.queue.RedisQueueService
import com.vietsub.server.plugins.configureHTTP
import com.vietsub.server.plugins.configureRouting
import com.vietsub.server.plugins.configureSerialization
import com.vietsub.server.services.*
import com.vietsub.server.storage.ObjectStorageService
import com.vietsub.server.storage.S3ObjectStorageService
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin

fun main() = io.ktor.server.netty.EngineMain.main(emptyArray())

fun Application.module() {
    val config = ServerConfig.fromEnvironment()
    DatabaseFactory.init(config)

    install(Koin) {
        modules(module {
            single { config }
            single { WebSocketHub() }
            single { DatabaseFactory.repository() }
            single { RedisQueueService(config) }
            single<ObjectStorageService> { S3ObjectStorageService(config) }
            single { AiService(config) }
            single { WhisperService(config) }
            single { TelegramBotService(config) }
            single { TelegramAdminService(config) }
            single { FeatureFlagService(config) }
            single { RenderJobService(config, get(), get(), get(), get()) }
            single { SystemMonitorService(config, get(), get(), get()) }
            single { CodeSyncService(config, get()) }
        })
    }

    configureSerialization()
    configureHTTP(config)
    configureRouting(config)
}
