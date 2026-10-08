package com.vietsub.core.util

actual object HmacSha256 {
    actual fun compute(key: ByteArray, data: ByteArray): ByteArray =
        error("HMAC-SHA256 must be validated server-side on macOS")
}
