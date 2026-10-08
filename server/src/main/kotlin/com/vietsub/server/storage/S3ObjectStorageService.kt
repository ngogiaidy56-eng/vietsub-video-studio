package com.vietsub.server.storage

import com.vietsub.server.ServerConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.core.sync.ResponseTransformer
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.HeadBucketRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import java.net.URI
import java.nio.file.Path
import java.time.Duration

class S3ObjectStorageService(private val config: ServerConfig) : ObjectStorageService {
    private val credentials = AwsBasicCredentials.create(config.storageAccessKey, config.storageSecretKey)
    private val client = S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.of(config.storageRegion))
        .apply { if (config.storageEndpoint.isNotBlank()) endpointOverride(URI.create(config.storageEndpoint)) }
        .forcePathStyle(config.storageEndpoint.isNotBlank())
        .build()
    private val presignClient = S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .region(Region.of(config.storageRegion))
        .apply { if (config.storagePresignEndpoint.isNotBlank()) endpointOverride(URI.create(config.storagePresignEndpoint)) }
        .forcePathStyle(config.storagePresignEndpoint.isNotBlank())
        .build()
    private val presigner = S3Presigner.builder()
        .s3Client(presignClient)
        .build()

    override suspend fun presignUpload(objectKey: String, contentType: String, expiresInSeconds: Long): PresignedUpload = withContext(Dispatchers.IO) {
        val request = PutObjectRequest.builder().bucket(config.storageBucket).key(objectKey).contentType(contentType).build()
        val presigned = presigner.presignPutObject(
            PutObjectPresignRequest.builder().putObjectRequest(request).signatureDuration(Duration.ofSeconds(expiresInSeconds)).build()
        )
        PresignedUpload(objectKey, presigned.url().toString(), expiresInSeconds)
    }

    override suspend fun presignDownload(objectKey: String, expiresInSeconds: Long): PresignedDownload = withContext(Dispatchers.IO) {
        if (config.storagePublicBaseUrl.isNotBlank()) {
            PresignedDownload(objectKey, "${config.storagePublicBaseUrl}/${objectKey.trimStart('/')}", expiresInSeconds)
        } else {
            val request = GetObjectRequest.builder().bucket(config.storageBucket).key(objectKey).build()
            val presigned = presigner.presignGetObject(
                GetObjectPresignRequest.builder().getObjectRequest(request).signatureDuration(Duration.ofSeconds(expiresInSeconds)).build()
            )
            PresignedDownload(objectKey, presigned.url().toString(), expiresInSeconds)
        }
    }

    override suspend fun put(objectKey: String, bytes: ByteArray, contentType: String) = withContext(Dispatchers.IO) {
        client.putObject(
            PutObjectRequest.builder().bucket(config.storageBucket).key(objectKey).contentType(contentType).build(),
            RequestBody.fromBytes(bytes)
        )
    }

    override suspend fun download(objectKey: String, destination: Path) = withContext(Dispatchers.IO) {
        destination.parent?.toFile()?.mkdirs()
        client.getObject(
            GetObjectRequest.builder().bucket(config.storageBucket).key(objectKey).build(),
            ResponseTransformer.toFile(destination)
        )
    }

    override suspend fun healthy(): Boolean = withContext(Dispatchers.IO) {
        runCatching { client.headBucket(HeadBucketRequest.builder().bucket(config.storageBucket).build()); true }.getOrDefault(false)
    }
}
