package com.notesdusecouriste.core.data.model

private fun MesureEntry.withNormalizedCirculation(): MesureEntry =
    copy(circulation = circulation.withMigratedTension())

fun MesuresBlock.normalized(): MesuresBlock {
    if (entries.isNotEmpty()) {
        return copy(
            entries = entries
                .map { it.withNormalizedCirculation() }
                .sortedBy { it.horodatageEpochMillis },
        )
    }
    val hasLegacy = horodatageEpochMillis > 0L ||
        respiration != RespirationMesure() ||
        circulation != CirculationMesure() ||
        conscience != ConscienceMesure() ||
        suspicionAvc != SuspicionAvcMesure()

    if (!hasLegacy) {
        return copy(entries = emptyList())
    }

    return copy(
        entries = listOf(
            MesureEntry(
                id = java.util.UUID.randomUUID().toString(),
                horodatageEpochMillis = horodatageEpochMillis.takeIf { it > 0L }
                    ?: System.currentTimeMillis(),
                respiration = respiration,
                circulation = circulation.withMigratedTension(),
                conscience = conscience,
                suspicionAvc = suspicionAvc,
            ),
        ),
    )
}

fun InterventionNoteContent.withNormalizedMesures(): InterventionNoteContent =
    copy(mesures = mesures.normalized())
