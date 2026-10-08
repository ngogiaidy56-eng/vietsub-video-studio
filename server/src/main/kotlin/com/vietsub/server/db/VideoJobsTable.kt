package com.vietsub.server.db

import org.jetbrains.exposed.sql.Table

object VideoJobsTable : Table("video_jobs") {
    val id = varchar("id", 64)
    val state = varchar("state", 32)
    val progress = integer("progress")
    val message = varchar("message", 2048)
    val inputPath = varchar("input_path", 2048)
    val outputPath = varchar("output_path", 2048)
    val inputObjectKey = varchar("input_object_key", 2048).nullable()
    val subtitleObjectKey = varchar("subtitle_object_key", 2048).nullable()
    val outputObjectKey = varchar("output_object_key", 2048).nullable()
    val idempotencyKey = varchar("idempotency_key", 128).nullable()
    val attempt = integer("attempt").default(0)
    val workerId = varchar("worker_id", 128).nullable()
    val error = text("error").nullable()
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    override val primaryKey = PrimaryKey(id)
}
