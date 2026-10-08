package com.vietsub.server.routes

import com.vietsub.server.ServerConfig
import com.vietsub.server.services.TelegramBotService
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import org.koin.ktor.ext.inject

@Serializable
data class TelegramWebhookUpdate(val update_id: Long? = null, val message: TelegramMessage? = null)
@Serializable
data class TelegramMessage(val chat: TelegramChat? = null, val text: String? = null)
@Serializable
data class TelegramChat(val id: Long? = null)

fun Route.telegramRoutes(config: ServerConfig) {
    val bot by inject<TelegramBotService>()
    post("/api/telegram/webhook") {
        val secret = call.request.headers["X-Telegram-Bot-Api-Secret-Token"]
        if (config.telegramWebhookSecret.isNotBlank() && secret != config.telegramWebhookSecret) return@post call.respond(HttpStatusCode.Unauthorized)
        val update = call.receive<TelegramWebhookUpdate>()
        val chatId = update.message?.chat?.id
        when (update.message?.text?.substringBefore(' ')) {
            "/start" -> chatId?.let { bot.answerStart(it) }
            "/status" -> chatId?.let { bot.sendMessage(it, "✅ Vietsub API online") }
            "/app" -> chatId?.let { bot.sendMessage(it, "🚀 Mở Mini App từ nút trong bot") }
        }
        call.respond(HttpStatusCode.OK)
    }
}
