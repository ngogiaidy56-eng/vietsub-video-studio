package com.vietsub.server.storage

import java.nio.file.Path

data class PresignedUpload(val objectKey: String, val uploadUrl: String, val expiresInSeconds: Long)

data class PresignedDownload(val objectKey: String, val downloadUrl: String, val expiresInSeconds: Long)

interface ObjectStorageService {
    suspend fun presignUpload(objectKey: String, contentType: String, expiresInSeconds: Long): PresignedUpload
    suspend fun presignDownload(objectKey: String, expiresInSeconds: Long): PresignedDownload
    suspend fun put(objectKey: String, bytes: ByteArray, contentType: String)
    suspend fun download(objectKey: String, destination: Path)
    suspend fun healthy(): Boolean
}
