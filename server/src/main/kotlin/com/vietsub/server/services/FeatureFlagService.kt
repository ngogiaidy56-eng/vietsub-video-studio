package com.vietsub.server.services

import com.vietsub.core.model.FeatureFlagConfig
import com.vietsub.server.ServerConfig
import kotlinx.coroutines.flow.MutableStateFlow

class FeatureFlagService(config: ServerConfig) {
    private val flags = MutableStateFlow(FeatureFlagConfig(enableRenderQueue = config.redisUrl.isNotBlank(), enableObjectStorage = config.storageDriver.isNotBlank()))
    fun current() = flags.value
    fun update(value: FeatureFlagConfig) { flags.value = value }
}
