package com.vietsub.app.ui.player

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun NativeVideoPlayer(videoUrl: String?, modifier: Modifier) {
    // Keep server-side validation and native playback ownership in the iOS launcher.
    Text("AVPlayer adapter: ${videoUrl ?: "no video"}")
}
