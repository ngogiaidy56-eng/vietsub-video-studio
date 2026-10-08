package com.vietsub.app

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vietsub.app.timeline.SmartMergeEngine
import com.vietsub.core.model.SubtitleItem
import com.vietsub.core.repository.SubtitleRepository

@Composable
fun App() {
    val repo = remember { SubtitleRepository() }
    val items by repo.subtitles.collectAsState()
    var tab by remember { mutableIntStateOf(0) }
    MaterialTheme(colorScheme = darkColorScheme()) {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Vietsub Video Studio Pro") }) },
            bottomBar = {
                NavigationBar {
                    listOf("Workspace", "Timeline", "Admin").forEachIndexed { index, label ->
                        NavigationBarItem(selected = tab == index, onClick = { tab = index }, icon = {}, label = { Text(label) })
                    }
                }
            }
        ) { padding ->
            when (tab) {
                0 -> WorkspaceScreen(padding, items)
                1 -> TimelineScreen(padding, repo, items)
                else -> AdminScreen(padding)
            }
        }
    }
}

@Composable
private fun WorkspaceScreen(padding: PaddingValues, items: List<SubtitleItem>) {
    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text("Production Workspace", style = MaterialTheme.typography.headlineSmall)
        Text("Upload → object storage → Redis queue → isolated FFmpeg worker → realtime WebSocket", color = MaterialTheme.colorScheme.onSurfaceVariant)
        ElevatedCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Render pipeline", style = MaterialTheme.typography.titleMedium)
                Text("Jobs: ${items.size} subtitle cues")
                LinearProgressIndicator(progress = { 1f }, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun TimelineScreen(padding: PaddingValues, repo: SubtitleRepository, items: List<SubtitleItem>) {
    Column(Modifier.fillMaxSize().padding(padding).padding(12.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Subtitle Timeline", style = MaterialTheme.typography.titleLarge)
            Button(onClick = {
                val n = items.size + 1
                val start = items.lastOrNull()?.endMs ?: 0L
                repo.replace(items + SubtitleItem(n.toString(), start, start + 2000, "New subtitle"))
            }) { Text("+ Cue") }
        }
        Spacer(Modifier.height(8.dp))
        if (items.isEmpty()) Text("No subtitle cues yet.")
        else items.forEach { cue ->
            ElevatedCard(Modifier.fillMaxWidth().padding(bottom = 6.dp)) {
                Column(Modifier.padding(10.dp)) {
                    Text("${cue.id}  ${cue.startMs} → ${cue.endMs} ms", style = MaterialTheme.typography.labelSmall)
                    Text(cue.translatedText ?: cue.text)
                }
            }
        }
        if (items.size >= 2) {
            Spacer(Modifier.height(8.dp))
            Text("Smart merge candidate: ${SmartMergeEngine.merge(items).size} cues after optimization", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun AdminScreen(padding: PaddingValues) {
    Column(Modifier.fillMaxSize().padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("System Control", style = MaterialTheme.typography.headlineSmall)
        AssistChip(onClick = {}, label = { Text("API + Redis + Storage") })
        AssistChip(onClick = {}, label = { Text("FFmpeg Worker Isolation") })
        AssistChip(onClick = {}, label = { Text("Service HMAC") })
        AssistChip(onClick = {}, label = { Text("Android/iOS signing in CI") })
    }
}
