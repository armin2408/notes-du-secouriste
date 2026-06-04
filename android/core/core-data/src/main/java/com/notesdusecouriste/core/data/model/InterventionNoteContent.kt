package com.notesdusecouriste.core.data.model

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class InterventionNoteContent(
    val victime: VictimeBlock = VictimeBlock(),
    val mesures: MesuresBlock = MesuresBlock(),
    val questionnaires: QuestionnairesBlock = QuestionnairesBlock(),
    val commentaire: String = "",
)

@Serializable
data class VictimeBlock(
    val nom: String = "",
    val prenom: String = "",
    val dateNaissance: String = "",
    val age: String = "",
    val ageOverride: Boolean = false,
    val coordonnees: String = "",
)

@Serializable
data class MesuresBlock(
    val entries: List<MesureEntry> = emptyList(),
    // Champs legacy (ancien snapshot unique) — migration vers entries
    val horodatageEpochMillis: Long = 0L,
    val respiration: RespirationMesure = RespirationMesure(),
    val circulation: CirculationMesure = CirculationMesure(),
    val conscience: ConscienceMesure = ConscienceMesure(),
    val suspicionAvc: SuspicionAvcMesure = SuspicionAvcMesure(),
)

@Serializable
data class MesureEntry(
    val id: String = "",
    val horodatageEpochMillis: Long = 0L,
    val respiration: RespirationMesure = RespirationMesure(),
    val circulation: CirculationMesure = CirculationMesure(),
    val conscience: ConscienceMesure = ConscienceMesure(),
    val glasgow: GlasgowMesure = GlasgowMesure(),
    val suspicionAvc: SuspicionAvcMesure = SuspicionAvcMesure(),
) {
    companion object {
        fun create(nowMillis: Long = System.currentTimeMillis()): MesureEntry =
            MesureEntry(
                id = UUID.randomUUID().toString(),
                horodatageEpochMillis = nowMillis,
            )
    }
}

@Serializable
data class RespirationMesure(
    val frequenceBpm: String = "",
    val amplitude: String = "",
    val regularite: String = "",
    val aspect: String = "",
    val saturationPct: String = "",
)

@Serializable
data class CirculationMesure(
    val frequenceBpm: String = "",
    val amplitude: String = "",
    val regularite: String = "",
    val aspect: String = "",
    val tensionSys: String = "",
    val tensionDia: String = "",
    /** Legacy — migré vers [tensionSys] / [tensionDia] à la lecture. */
    val tensionArterielle: String = "",
    val trc: String = "",
) {
    fun withMigratedTension(): CirculationMesure {
        if (tensionSys.isNotEmpty() || tensionDia.isNotEmpty()) return this
        if (tensionArterielle.isBlank()) return this
        val parts = tensionArterielle.split("/", "-", " ")
            .map { it.trim().filter(Char::isDigit) }
            .filter { it.isNotEmpty() }
        return copy(
            tensionSys = parts.getOrElse(0) { "" },
            tensionDia = parts.getOrElse(1) { "" },
        )
    }
}

@Serializable
data class ConscienceMesure(
    val conscience: String = "",
    val orientationTemps: String = "",
    val orientationEspace: String = "",
    val propos: String = "",
    /** Format attendu: XX,XX (°C). */
    val temperatureC: String = "",
    /** Valeur glycémie (chiffres uniquement). */
    val glycemie: String = "",
    val glycemieUnit: GlycemieUnit = GlycemieUnit.MgDl,
)

@Serializable
enum class GlycemieUnit {
    MgDl,
    MmolL,
    GL,
}

@Serializable
data class GlasgowMesure(
    /** Score 1–4, clé = valeur numérique sous forme de texte. */
    val ouvertureYeux: String = "",
    /** Score 1–5. */
    val reponseVerbale: String = "",
    /** Score 1–6. */
    val reponseMotrice: String = "",
)

@Serializable
data class SuspicionAvcMesure(
    val visage: String = "",
    val pupillesEgales: String = "",
    val pupillesReactives: String = "",
    val motriciteBras: String = "",
    val parole: String = "",
    val heureSymptomes: String = "",
)

@Serializable
data class QuestionnairesBlock(
    val sample: SampleQuestionnaire = SampleQuestionnaire(),
    val opqrst: OpqrstQuestionnaire = OpqrstQuestionnaire(),
)

@Serializable
data class SampleQuestionnaire(
    /** S: Signs / Symptoms */
    val signesSymptomes: String = "",
    /** A: Allergies */
    val allergies: String = "",
    /** M: Medications */
    val medicaments: String = "",
    /** P: Past medical history */
    val antecedents: String = "",
    /** L: Last oral intake */
    val dernierRepas: String = "",
    /** E: Events leading up */
    val evenements: String = "",
)

@Serializable
data class OpqrstQuestionnaire(
    /** O: Onset */
    val debut: String = "",
    /** P: Provocation / Palliation */
    val provocationPalliation: String = "",
    /** Q: Quality */
    val qualite: String = "",
    /** R: Région */
    val region: String = "",
    /** S: Severity */
    val severite: String = "",
    /** T: Time */
    val temps: String = "",
)
