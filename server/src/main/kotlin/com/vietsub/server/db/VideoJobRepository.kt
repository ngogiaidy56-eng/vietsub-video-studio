package com.vietsub.server.db

import com.vietsub.core.model.JobStatus
import com.vietsub.core.model.RenderQueueMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

class VideoJobRepository(private val db: Database) {
    suspend fun createIfAbsent(
        status: JobStatus,
        idempotencyKey: String? = null
    ): JobStatus = withContext(Dispatchers.IO) {
        transaction(db) {
            val exists = VideoJobsTable.selectAll().where { VideoJobsTable.id eq status.jobId }.singleOrNull()
            if (exists == null) {
                val now = System.currentTimeMillis()
                VideoJobsTable.insert {
                    it[id] = status.jobId
                    it[state] = status.state
                    it[progress] = status.progress
                    it[message] = status.message
                    it[inputPath] = ""
                    it[outputPath] = ""
                    it[inputObjectKey] = status.inputObjectKey
                    it[subtitleObjectKey] = status.subtitleObjectKey
                    it[outputObjectKey] = status.outputObjectKey
                    it[VideoJobsTable.idempotencyKey] = idempotencyKey
                    it[attempt] = status.attempt
                    it[workerId] = status.workerId
                    it[error] = status.error
                    it[createdAt] = now
                    it[updatedAt] = now
                }
            }
            status
        }
    }

    suspend fun find(id: String): JobStatus? = withContext(Dispatchers.IO) {
        transaction(db) {
            VideoJobsTable.selectAll().where { VideoJobsTable.id eq id }.singleOrNull()?.toStatus()
        }
    }

    suspend fun findByIdempotencyKey(key: String): JobStatus? = withContext(Dispatchers.IO) {
        transaction(db) {
            VideoJobsTable.selectAll().where { VideoJobsTable.idempotencyKey eq key }.limit(1).firstOrNull()?.toStatus()
        }
    }

    suspend fun update(update: JobStatus) = withContext(Dispatchers.IO) {
        transaction(db) {
            VideoJobsTable.update({ VideoJobsTable.id eq update.jobId }) {
                it[state] = update.state
                it[progress] = update.progress
                it[message] = update.message
                it[error] = update.error
                update.outputObjectKey?.let { key -> it[outputObjectKey] = key }
                it[attempt] = update.attempt
                it[workerId] = update.workerId
                it[updatedAt] = System.currentTimeMillis()
            }
        }
    }

    suspend fun countActive(): Int = withContext(Dispatchers.IO) {
        transaction(db) {
            VideoJobsTable.select(VideoJobsTable.id)
                .where { VideoJobsTable.state inList listOf("QUEUED", "RUNNING", "RETRYING") }
                .count().toInt()
        }
    }

    private fun ResultRow.toStatus() = JobStatus(
        jobId = this[VideoJobsTable.id],
        state = this[VideoJobsTable.state],
        progress = this[VideoJobsTable.progress],
        message = this[VideoJobsTable.message],
        error = this[VideoJobsTable.error],
        inputObjectKey = this[VideoJobsTable.inputObjectKey],
        subtitleObjectKey = this[VideoJobsTable.subtitleObjectKey],
        outputObjectKey = this[VideoJobsTable.outputObjectKey],
        attempt = this[VideoJobsTable.attempt],
        workerId = this[VideoJobsTable.workerId]
    )
}
