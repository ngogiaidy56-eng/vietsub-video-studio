package com.vietsub.core.util
actual object HmacSha256 {
    actual fun compute(key: ByteArray, data: ByteArray): ByteArray = error("Use server validation for Telegram initData on JS")
}
