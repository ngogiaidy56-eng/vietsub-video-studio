package com.vietsub.server.plugins

import com.vietsub.server.ServerConfig
import com.vietsub.server.security.ServiceAuth
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.http.*

fun authEnabled(config: ServerConfig) = config.serviceAuthSecret.isNotBlank()

suspend fun ApplicationCall.requireServiceAuth(config: ServerConfig, body: ByteArray): Boolean {
    val headers = mapOf(
        ServiceAuth.SERVICE_ID to request.headers[ServiceAuth.SERVICE_ID].orEmpty(),
        ServiceAuth.TIMESTAMP to request.headers[ServiceAuth.TIMESTAMP].orEmpty(),
        ServiceAuth.SIGNATURE to request.headers[ServiceAuth.SIGNATURE].orEmpty()
    )
    if (!ServiceAuth.verify(headers, config.serviceAuthSecret, request.httpMethod.value, request.uri.substringBefore('?'), body)) {
        respond(HttpStatusCode.Unauthorized, mapOf("error" to "invalid service signature"))
        return false
    }
    return true
}
