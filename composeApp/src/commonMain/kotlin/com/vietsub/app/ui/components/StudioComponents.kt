package com.vietsub.app.ui.components
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
@Composable fun StudioStatusCard(label: String, value: String) = ElevatedCard { Column { Text(label); Text(value) } }
