package com.vietsub.server.services

import com.vietsub.server.ServerConfig

class CodeSyncService(private val config: ServerConfig, private val bot: TelegramBotService) {
    suspend fun healthAlert(text: String) {
        config.telegramAdminIds.forEach { bot.sendMessage(it, "🛠️ CodeSync: $text") }
    }
}
