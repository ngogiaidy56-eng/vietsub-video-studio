package com.vietsub.server.services

import com.vietsub.server.ServerConfig
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.forms.*
import io.ktor.http.*
import java.nio.file.Path

class WhisperService(private val config: ServerConfig) {
    private val client = HttpClient(CIO)

    suspend fun transcribe(audio: Path, language: String? = null): String {
        require(config.openAiApiKey.isNotBlank()) { "OPENAI_API_KEY is not configured" }
        return client.submitFormWithBinaryData("https://api.openai.com/v1/audio/transcriptions", formData {
            append("model", "whisper-1")
            append("response_format", "srt")
            language?.let { append("language", it) }
            append("file", audio.toFile().readBytes(), Headers.build {
                append(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString())
                append(HttpHeaders.ContentDisposition, "filename=\"${audio.fileName}\"")
            })
        }) {
            header(HttpHeaders.Authorization, "Bearer ${config.openAiApiKey}")
        }.body<String>()
    }
}
