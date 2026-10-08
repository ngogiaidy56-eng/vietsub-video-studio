package com.vietsub.worker

import com.vietsub.core.model.SubtitleStyle
import java.nio.file.Path

object FFmpegCommandBuilder {
    fun build(input: Path, subtitle: Path?, output: Path, style: SubtitleStyle): List<String> {
        val command = mutableListOf("ffmpeg", "-hide_banner", "-nostdin", "-y", "-i", input.toString())
        if (subtitle != null) {
            command += listOf("-vf", subtitleFilter(subtitle, style))
        }
        command += listOf("-c:v", "libx264", "-preset", "medium", "-crf", "18", "-c:a", "aac", "-b:a", "192k", "-movflags", "+faststart", "-progress", "pipe:1", "-nostats", output.toString())
        return command
    }

    private fun subtitleFilter(path: Path, style: SubtitleStyle): String {
        val escaped = path.toString().replace("\\", "\\\\").replace(":", "\\:").replace("'", "\\'")
        val primary = validAssColor(style.primaryColor)
        val outline = validAssColor(style.outlineColor)
        val font = style.fontFamily.replace("'", "").take(64)
        val bold = if (style.bold) -1 else 0
        val italic = if (style.italic) -1 else 0
        return "subtitles='$escaped':force_style='FontName=$font,FontSize=${style.fontSize},Bold=$bold,Italic=$italic,PrimaryColour=$primary,OutlineColour=$outline,Outline=${style.outlineWidth},Alignment=${style.alignment},MarginV=${style.marginV}'"
    }

    private fun validAssColor(value: String): String = requireNotNull(Regex("&H[0-9A-Fa-f]{8}").matchEntire(value)) { "Invalid ASS color" }.value
}
