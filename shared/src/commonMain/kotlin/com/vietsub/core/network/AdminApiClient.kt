package com.vietsub.core.network
class AdminApiClient(private val client: RenderApiClient) {
    suspend fun status() = client.health()
}
