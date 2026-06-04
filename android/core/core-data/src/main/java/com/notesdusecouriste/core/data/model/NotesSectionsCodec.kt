package com.notesdusecouriste.core.data.model

import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

object NotesSectionsCodec {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun decode(raw: String): InterventionNoteContent =
        when {
            raw.isBlank() || raw == "{}" -> InterventionNoteContent().withNormalizedMesures()
            else -> runCatching { json.decodeFromString<InterventionNoteContent>(raw) }
                .getOrDefault(InterventionNoteContent())
                .withNormalizedMesures()
        }

    fun encode(content: InterventionNoteContent): String {
        val normalized = content.withNormalizedMesures()
        val persist = normalized.copy(
            mesures = MesuresBlock(entries = normalized.mesures.entries),
        )
        return json.encodeToString(serializer(), persist)
    }
}
