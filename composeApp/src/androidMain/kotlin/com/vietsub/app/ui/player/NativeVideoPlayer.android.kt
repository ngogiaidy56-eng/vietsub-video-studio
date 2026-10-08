package com.vietsub.app.ui.player

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
actual fun NativeVideoPlayer(videoUrl: String?, modifier: Modifier) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val player = remember(videoUrl) {
        ExoPlayer.Builder(context).build().apply {
            videoUrl?.let { setMediaItem(MediaItem.fromUri(Uri.parse(it))) }
            prepare()
        }
    }
    DisposableEffect(player) { onDispose { player.release() } }
    AndroidView(modifier = modifier, factory = { PlayerView(it).apply { this.player = player } })
}
