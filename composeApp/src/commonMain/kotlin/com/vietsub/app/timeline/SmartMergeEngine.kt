package com.vietsub.app.timeline

import com.vietsub.core.model.SubtitleItem

object SmartMergeEngine {
    fun merge(items: List<SubtitleItem>, maxGapMs: Long = 450, maxChars: Int = 80): List<SubtitleItem> {
        if (items.isEmpty()) return emptyList()
        val result = mutableListOf<SubtitleItem>()
        var current = items.sortedBy { it.startMs }.first()
        for (next in items.sortedBy { it.startMs }.drop(1)) {
            val gap = next.startMs - current.endMs
            val joined = (current.text + " " + next.text).trim()
            if (gap <= maxGapMs && joined.length <= maxChars) {
                current = current.copy(endMs = next.endMs, text = joined, translatedText = null)
            } else {
                result += current
                current = next
            }
        }
        result += current
        return result
    }
}
