package com.vietsub.server.services

import com.vietsub.server.ServerConfig

class TelegramAdminService(private val config: ServerConfig) {
    fun isAdmin(userId: Long) = userId in config.telegramAdminIds
}
