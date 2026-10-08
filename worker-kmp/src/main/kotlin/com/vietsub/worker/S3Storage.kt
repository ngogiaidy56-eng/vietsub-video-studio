package com.vietsub.worker

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.sync.ResponseTransformer
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.core.sync.RequestBody
import java.net.URI
import java.nio.file.Path

class S3Storage(private val config: WorkerConfig) {
    private val client = S3Client.builder()
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(config.storageAccessKey, config.storageSecretKey)))
        .region(Region.of(config.storageRegion))
        .apply { if (config.storageEndpoint.isNotBlank()) endpointOverride(URI.create(config.storageEndpoint)) }
        .forcePathStyle(config.storageEndpoint.isNotBlank())
        .build()

    suspend fun download(key: String, target: Path) = withContext(Dispatchers.IO) {
        target.parent?.toFile()?.mkdirs()
        client.getObject(GetObjectRequest.builder().bucket(config.storageBucket).key(key).build(), ResponseTransformer.toFile(target))
    }

    suspend fun upload(key: String, file: Path, contentType: String) = withContext(Dispatchers.IO) {
        client.putObject(
            PutObjectRequest.builder().bucket(config.storageBucket).key(key).contentType(contentType).build(),
            RequestBody.fromFile(file)
        )
    }
}
