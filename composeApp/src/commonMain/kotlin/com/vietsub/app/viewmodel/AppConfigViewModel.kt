package com.vietsub.app.viewmodel
import com.vietsub.core.repository.SystemConfigRepository
class AppConfigViewModel(private val repository: SystemConfigRepository) { val state get() = repository.serverStatus }
