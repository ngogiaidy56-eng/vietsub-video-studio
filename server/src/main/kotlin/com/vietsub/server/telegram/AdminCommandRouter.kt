package com.vietsub.server.telegram
import com.vietsub.server.services.TelegramBotService
class AdminCommandRouter(private val bot: TelegramBotService) { suspend fun handle(chatId: Long, command: String) { if (command == "/start") bot.answerStart(chatId) } }
