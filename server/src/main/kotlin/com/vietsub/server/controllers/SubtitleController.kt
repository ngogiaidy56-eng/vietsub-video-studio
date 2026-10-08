package com.vietsub.server.controllers
import com.vietsub.core.model.TranslateRequest
import com.vietsub.core.model.TranslateResponse
import com.vietsub.server.services.AiService
class SubtitleController(private val ai: AiService) {
    suspend fun translate(request: TranslateRequest) = TranslateResponse(ai.translateSrt(request.srt, request.targetLanguage))
}
