package com.vietsub.core.util

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
actual object HmacSha256 {
    actual fun compute(key: ByteArray, data: ByteArray): ByteArray {
        val mac = Mac.getInstance("HmacSHA256")
        mac.init(SecretKeySpec(key, "HmacSHA256"))
        return mac.doFinal(data)
    }
}
