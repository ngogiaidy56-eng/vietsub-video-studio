package com.vietsub.core.repository

import com.vietsub.core.model.SubtitleItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SubtitleRepository {
    private val state = MutableStateFlow<List<SubtitleItem>>(emptyList())
    val subtitles: StateFlow<List<SubtitleItem>> = state.asStateFlow()

    fun replace(items: List<SubtitleItem>) { state.value = items.sortedBy { it.startMs } }
    fun update(id: String, transform: (SubtitleItem) -> SubtitleItem) {
        state.value = state.value.map { if (it.id == id) transform(it) else it }.sortedBy { it.startMs }
    }
    fun delete(id: String) { state.value = state.value.filterNot { it.id == id } }
}
