package com.vietsub.core.repository

import com.vietsub.core.model.FeatureFlagConfig
import com.vietsub.core.model.ServerStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SystemConfigRepository {
    private val statusState = MutableStateFlow<ServerStatus?>(null)
    private val flagsState = MutableStateFlow(FeatureFlagConfig())
    val serverStatus: StateFlow<ServerStatus?> = statusState.asStateFlow()
    val featureFlags: StateFlow<FeatureFlagConfig> = flagsState.asStateFlow()
    fun updateStatus(value: ServerStatus) { statusState.value = value }
    fun updateFlags(value: FeatureFlagConfig) { flagsState.value = value }
}
