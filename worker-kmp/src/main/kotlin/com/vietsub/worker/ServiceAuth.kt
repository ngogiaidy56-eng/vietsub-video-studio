package com.vietsub.worker

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object ServiceAuth {
    const val SERVICE_ID = "X-Service-Id"
    const val TIMESTAMP = "X-Service-Timestamp"
    const val SIGNATURE = "X-Service-Signature"

    fun sign(serviceId: String, secret: String, method: String, path: String, timestampMs: Long, body: ByteArray): String {
        val bodyHash = sha256Hex(body)
        val canonical = "$serviceId\n${method.uppercase()}\n$path\n$timestampMs\n$bodyHash"
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(secret.toByteArray(StandardCharsets.UTF_8), "HmacSHA256"))
        return mac.doFinal(canonical.toByteArray(StandardCharsets.UTF_8)).joinToString("") { "%02x".format(it) }
    }

    private fun sha256Hex(bytes: ByteArray): String = MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) }
}
