package com.vietsub.server.db

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import com.vietsub.server.ServerConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    lateinit var db: Database
        private set

    fun init(config: ServerConfig) {
        config.dataDir.toFile().mkdirs()
        val pool = HikariConfig().apply {
            jdbcUrl = config.databaseUrl
            username = config.databaseUser
            password = config.databasePassword
            maximumPoolSize = config.databasePoolSize
            minimumIdle = minOf(2, config.databasePoolSize)
            isAutoCommit = false
            connectionTimeout = 10_000
            validationTimeout = 5_000
            leakDetectionThreshold = 0
            driverClassName = driverFor(config.databaseUrl)
        }
        db = Database.connect(HikariDataSource(pool))
        transaction(db) { SchemaUtils.createMissingTablesAndColumns(VideoJobsTable) }
    }

    fun repository(): VideoJobRepository = VideoJobRepository(db)

    private fun driverFor(url: String): String = when {
        url.startsWith("jdbc:postgresql") -> "org.postgresql.Driver"
        else -> "org.h2.Driver"
    }
}
