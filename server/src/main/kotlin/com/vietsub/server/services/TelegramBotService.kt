package com.vietsub.server.services

import com.vietsub.server.ServerConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class TelegramBotService(private val config: ServerConfig) {
    private val client = HttpClient(CIO)

    suspend fun sendMessage(chatId: Long, text: String) {
        if (config.telegramBotToken.isBlank()) return
        client.post("https://api.telegram.org/bot${config.telegramBotToken}/sendMessage") {
            contentType(ContentType.Application.Json)
            setBody(buildJsonObject { put("chat_id", chatId); put("text", text) })
        }
    }

    suspend fun answerStart(chatId: Long) = sendMessage(chatId, "🚀 Vietsub Video Studio\n/app – mở Mini App\n/status – trạng thái hệ thống")
}
