package com.vietsub.app.timeline

import com.vietsub.core.model.SubtitleItem
import kotlin.test.Test
import kotlin.test.assertEquals

class SmartMergeEngineTest {
    @Test
    fun mergesShortAdjacentCues() {
        val input = listOf(
            SubtitleItem("1", 0, 1000, "Xin chao"),
            SubtitleItem("2", 1200, 2200, "ban")
        )
        val result = SmartMergeEngine.merge(input)
        assertEquals(1, result.size)
        assertEquals("Xin chao ban", result.single().text)
    }
}
