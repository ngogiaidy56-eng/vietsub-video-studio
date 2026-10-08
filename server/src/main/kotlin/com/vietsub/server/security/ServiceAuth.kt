package com.vietsub.server.security

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object ServiceAuth {
    const val SERVICE_ID = "X-Service-Id"
    const val TIMESTAMP = "X-Service-Timestamp"
    const val SIGNATURE = "X-Service-Signature"
    private const val MAX_SKEW_MS = 5 * 60 * 1000L

    fun verify(headers: Map<String, String>, secret: String, method: String, path: String, body: ByteArray, nowMs: Long = System.currentTimeMillis()): Boolean {
        if (secret.isBlank()) return false
        val serviceId = headers[SERVICE_ID] ?: return false
        val timestamp = headers[TIMESTAMP]?.toLongOrNull() ?: return false
        val signature = headers[SIGNATURE] ?: return false
        if (kotlin.math.abs(nowMs - timestamp) > MAX_SKEW_MS) return false
        val expected = sign(serviceId, secret, method, path, timestamp, body)
        return MessageDigest.isEqual(expected.toByteArray(StandardCharsets.US_ASCII), signature.toByteArray(StandardCharsets.US_ASCII))
    }

    private fun sign(serviceId: String, secret: String, method: String, path: String, timestampMs: Long, body: ByteArray): String {
        val bodyHash = MessageDigest.getInstance("SHA-256").digest(body).joinToString("") { "%02x".format(it) }
        val canonical = "$serviceId\n${method.uppercase()}\n$path\n$timestampMs\n$bodyHash"
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        return mac.doFinal(canonical.toByteArray(StandardCharsets.UTF_8)).joinToString("") { "%02x".format(it) }
    }
}
