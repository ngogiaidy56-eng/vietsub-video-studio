package com.vietsub.server.services

import com.vietsub.server.ServerConfig
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.*

class AiService(private val config: ServerConfig) {
    private val client = HttpClient(CIO) { install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) } }

    suspend fun translateSrt(srt: String, targetLanguage: String): String {
        require(config.geminiApiKey.isNotBlank()) { "GEMINI_API_KEY is not configured" }
        val prompt = "Translate subtitle text to $targetLanguage. Preserve numbering and timestamps exactly. Return only SRT.\n\n$srt"
        val response: JsonObject = client.post("https://generativelanguage.googleapis.com/v1beta/models/${config.geminiModel}:generateContent") {
            parameter("key", config.geminiApiKey)
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject {
                putJsonArray("contents") {
                    addJsonObject { putJsonArray("parts") { addJsonObject { put("text", prompt) } } }
                }
                putJsonObject("generationConfig") { put("temperature", 0.15); put("maxOutputTokens", 8192) }
            })
        }.body()
        return response["candidates"]?.jsonArray?.firstOrNull()?.jsonObject?.get("content")?.jsonObject
            ?.get("parts")?.jsonArray?.firstOrNull()?.jsonObject?.get("text")?.jsonPrimitive?.content
            ?.replace("```srt", "", ignoreCase = true)?.replace("```", "")?.trim()
            ?: error("Gemini returned no text")
    }
}
