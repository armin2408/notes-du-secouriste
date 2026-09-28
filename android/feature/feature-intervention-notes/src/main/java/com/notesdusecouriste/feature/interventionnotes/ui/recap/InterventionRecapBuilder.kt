package com.notesdusecouriste.feature.interventionnotes.ui.recap



import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Accessibility
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Medication
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Sick
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.WarningAmber

import com.notesdusecouriste.core.data.model.InterventionNoteContent

import com.notesdusecouriste.core.data.model.QuestionnairesBlock

import com.notesdusecouriste.core.data.model.VictimeBlock

import com.notesdusecouriste.core.data.model.formatDateNaissanceDisplay

import com.notesdusecouriste.feature.interventionnotes.R



object InterventionRecapBuilder {



    fun build(context: Context, content: InterventionNoteContent): InterventionRecap {

        val identityLines = buildIdentityLines(context, content.victime)

        val measuresTable = MesureRecapTableBuilder.build(context, content.mesures.entries)

        val questionnaires = buildQuestionnaires(context, content.questionnaires)

        val comment = content.commentaire.trim().takeIf { it.isNotEmpty() }

        return InterventionRecap(

            identityLines = identityLines,

            measuresTable = measuresTable,

            questionnaires = questionnaires,

            comment = comment,

        )

    }



    private fun buildIdentityLines(context: Context, victime: VictimeBlock): List<RecapLine> =
        buildList {
            val displayName = listOf(victime.prenom.trim(), victime.nom.trim())
                .filter { it.isNotEmpty() }
                .joinToString(" ")
            if (displayName.isNotEmpty()) {
                add(
                    RecapLine(
                        label = context.getString(R.string.pdf_label_prenom_nom),
                        value = displayName,
                    ),
                )
            }
            val dob = victime.dateNaissance.trim()
            if (dob.isNotEmpty()) {
                add(
                    RecapLine(
                        label = context.getString(R.string.field_date_naissance),
                        value = formatDateNaissanceDisplay(dob),
                    ),
                )
            }
            victime.age.trim().takeIf { it.isNotEmpty() }?.let { age ->
                add(
                    RecapLine(
                        label = context.getString(R.string.field_age),
                        value = context.getString(R.string.recap_age_value, age),
                    ),
                )
            }
            line(context, R.string.field_coordonnees, victime.coordonnees)?.let(::add)
        }



    private fun line(context: Context, labelRes: Int, raw: String): RecapLine? {

        val value = raw.trim()

        if (value.isEmpty()) return null

        return RecapLine(label = context.getString(labelRes), value = value)

    }

    private fun buildQuestionnaires(context: Context, questionnaires: QuestionnairesBlock): List<RecapSubSection> =
        buildList {
            val sampleLines = buildList {
                questionnaires.sample.signesSymptomes.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "S — Signes / Symptômes", value = it, icon = Icons.Outlined.Sick))
                }
                questionnaires.sample.allergies.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "A — Allergies", value = it, icon = Icons.Outlined.WarningAmber))
                }
                questionnaires.sample.medicaments.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "M — Médicaments", value = it, icon = Icons.Outlined.Medication))
                }
                questionnaires.sample.antecedents.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "P — Antécédents", value = it, icon = Icons.Outlined.History))
                }
                questionnaires.sample.dernierRepas.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "L — Dernier repas / boisson", value = it, icon = Icons.Outlined.Restaurant))
                }
                questionnaires.sample.evenements.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "E — Événements", value = it, icon = Icons.Outlined.Description))
                }
            }
            if (sampleLines.isNotEmpty()) {
                add(RecapSubSection(title = "SAMPLE", lines = sampleLines))
            }

            val opqrstLines = buildList {
                questionnaires.opqrst.debut.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "O — Début", value = it, icon = Icons.Outlined.PlayCircle))
                }
                questionnaires.opqrst.provocationPalliation.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "P — Provocation / Soulagement", value = it, icon = Icons.Outlined.SwapVert))
                }
                questionnaires.opqrst.qualite.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "Q — Qualité", value = it, icon = Icons.Outlined.Bolt))
                }
                questionnaires.opqrst.region.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "R — Région", value = it, icon = Icons.Outlined.Accessibility))
                }
                questionnaires.opqrst.severite.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "S — Sévérité", value = it, icon = Icons.Outlined.Speed))
                }
                questionnaires.opqrst.temps.trim().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(label = "T — Temps / Évolution", value = it, icon = Icons.Outlined.Timeline))
                }
            }
            if (opqrstLines.isNotEmpty()) {
                add(RecapSubSection(title = "OPQRST", lines = opqrstLines))
            }
        }

}


