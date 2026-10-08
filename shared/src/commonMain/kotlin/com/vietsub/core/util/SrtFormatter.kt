package com.vietsub.core.util

import com.vietsub.core.model.SubtitleItem

object SrtFormatter {
    private val timestamp = Regex("(\\d{2}):(\\d{2}):(\\d{2})[,.:](\\d{3})")

    fun parse(srt: String): List<SubtitleItem> {
        val blocks = srt.replace("\r", "").trim().split(Regex("\\n\\s*\\n"))
        return blocks.mapNotNull { block ->
            val lines = block.lines()
            if (lines.size < 2) return@mapNotNull null
            val timingIndex = lines.indexOfFirst { it.contains(" --> ") }
            if (timingIndex < 0) return@mapNotNull null
            val timing = lines[timingIndex].split(" --> ")
            if (timing.size != 2) return@mapNotNull null
            val start = parseTime(timing[0]) ?: return@mapNotNull null
            val end = parseTime(timing[1]) ?: return@mapNotNull null
            if (end <= start) return@mapNotNull null
            val text = lines.drop(timingIndex + 1).joinToString("\n").trim()
            SubtitleItem(
                id = (lines.firstOrNull()?.trim()?.toIntOrNull() ?: 0).toString(),
                startMs = start,
                endMs = end,
                text = text
            )
        }
    }

    fun encode(items: List<SubtitleItem>): String = items.mapIndexed { index, item ->
        buildString {
            append(index + 1).append('\n')
            append(formatTime(item.startMs)).append(" --> ").append(formatTime(item.endMs)).append('\n')
            append((item.translatedText ?: item.text).trim()).append('\n')
        }
    }.joinToString("\n")

    private fun parseTime(value: String): Long? {
        val match = timestamp.find(value.trim()) ?: return null
        val (hh, mm, ss, ms) = match.destructured
        return hh.toLong() * 3_600_000 + mm.toLong() * 60_000 + ss.toLong() * 1_000 + ms.toLong()
    }

    private fun formatTime(value: Long): String {
        val safe = value.coerceAtLeast(0)
        val h = safe / 3_600_000
        val m = (safe % 3_600_000) / 60_000
        val s = (safe % 60_000) / 1_000
        val ms = safe % 1_000
        return "%02d:%02d:%02d,%03d".format(h, m, s, ms)
    }
}
