package com.vietsub.core.util

/** Telegram WebApp initData validation helper. Server-side validation is preferred. */
object TelegramAuthValidator {
    fun validate(initData: String, botToken: String, maxAgeSeconds: Long = 86_400): Boolean {
        if (initData.isBlank() || botToken.isBlank()) return false
        val params = initData.split('&').mapNotNull { part ->
            val idx = part.indexOf('=')
            if (idx <= 0) null else part.substring(0, idx) to part.substring(idx + 1)
        }.toMap()
        val hash = params["hash"] ?: return false
        val authDate = params["auth_date"]?.toLongOrNull() ?: return false
        val now = currentEpochSeconds()
        if (now - authDate > maxAgeSeconds || authDate > now + 60) return false
        val dataCheckString = params.filterKeys { it != "hash" }.toSortedMap()
            .entries.joinToString("\n") { "${it.key}=${it.value}" }
        val secret = hmacSha256("WebAppData".encodeToByteArray(), botToken.encodeToByteArray())
        val actual = hmacSha256(secret, dataCheckString.encodeToByteArray()).toHex()
        return constantTimeEquals(actual, hash)
    }

    private fun currentEpochSeconds(): Long = kotlin.time.Clock.System.now().epochSeconds
    private fun constantTimeEquals(a: String, b: String): Boolean {
        if (a.length != b.length) return false
        var diff = 0
        for (i in a.indices) diff = diff or (a[i].code xor b[i].code)
        return diff == 0
    }
    private fun hmacSha256(key: ByteArray, data: ByteArray): ByteArray = HmacSha256.compute(key, data)
}

expect object HmacSha256 {
    fun compute(key: ByteArray, data: ByteArray): ByteArray
}

private fun ByteArray.toHex(): String = joinToString("") { "%02x".format(it.toInt() and 0xff) }
