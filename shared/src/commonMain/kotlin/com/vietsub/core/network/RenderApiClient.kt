package com.vietsub.core.network

import com.vietsub.core.model.JobStatus
import com.vietsub.core.model.RenderRequest
import com.vietsub.core.model.ServerStatus
import com.vietsub.core.model.TranslateRequest
import com.vietsub.core.model.TranslateResponse
import com.vietsub.core.model.UploadPresignRequest
import com.vietsub.core.model.UploadPresignResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class RenderApiClient(
    private val baseUrl: String,
    private val client: HttpClient = HttpClient { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }
) {
    suspend fun health(): ServerStatus = client.get("$baseUrl/api/v1/status").body()
    suspend fun presignUpload(request: UploadPresignRequest): UploadPresignResponse = client.post("$baseUrl/api/v1/uploads/presign") { setBody(request) }.body()
    suspend fun translate(request: TranslateRequest): TranslateResponse = client.post("$baseUrl/api/v1/subtitles/translate") { setBody(request) }.body()
    suspend fun submitRender(request: RenderRequest): JobStatus = client.post("$baseUrl/api/v1/render") { setBody(request) }.body()
    suspend fun job(jobId: String): JobStatus = client.get("$baseUrl/api/v1/jobs/$jobId").body()
}
