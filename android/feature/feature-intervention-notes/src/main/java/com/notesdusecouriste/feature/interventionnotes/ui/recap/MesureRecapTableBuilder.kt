package com.notesdusecouriste.feature.interventionnotes.ui.recap



import android.content.Context

import com.notesdusecouriste.core.data.model.CirculationMesure

import com.notesdusecouriste.core.data.model.GlycemieUnit

import com.notesdusecouriste.core.data.model.MesureEntry

import com.notesdusecouriste.feature.interventionnotes.R

import com.notesdusecouriste.core.data.model.GlasgowMesure
import com.notesdusecouriste.feature.interventionnotes.ui.GlasgowChoices
import com.notesdusecouriste.feature.interventionnotes.ui.GlasgowOption
import com.notesdusecouriste.feature.interventionnotes.ui.MesureChoices
import com.notesdusecouriste.feature.interventionnotes.ui.totalScore

import com.notesdusecouriste.feature.interventionnotes.ui.components.ChoiceOption

import com.notesdusecouriste.feature.interventionnotes.ui.formatMesureTabLabel
import com.notesdusecouriste.feature.interventionnotes.ui.mesureTabLabelsShowDate



internal object MesureRecapTableBuilder {



    fun build(context: Context, entries: List<MesureEntry>): RecapMeasuresTable? {

        if (entries.isEmpty()) return null



        val sorted = entries.sortedBy { it.horodatageEpochMillis }
        val showDate = mesureTabLabelsShowDate(sorted)
        val headers = sorted.map { formatMesureTabLabel(it.horodatageEpochMillis, showDate) }

        val emptyCell = context.getString(R.string.recap_table_empty_cell)



        val tableRows = buildList {

            for (group in measureGroups(context)) {

                val measureRows = group.definitions.mapNotNull { def ->

                    buildMeasureRow(sorted, def, emptyCell)

                }

                if (measureRows.isEmpty()) continue

                add(RecapTableRow.SectionHeader(group.subtitle))

                addAll(measureRows)

            }

        }



        if (tableRows.isEmpty()) return null

        return RecapMeasuresTable(columnHeaders = headers, rows = tableRows)

    }



    private fun buildMeasureRow(

        sorted: List<MesureEntry>,

        def: MeasureRowDef,

        emptyCell: String,

    ): RecapTableRow.Measure? {

        val cells = sorted.map { entry -> def.extract(entry, emptyCell) }

        if (cells.all { it.text == emptyCell || it.text.isBlank() }) return null

        return RecapTableRow.Measure(label = def.label, cells = cells)

    }



    private data class MeasureGroup(

        val subtitle: String,

        val definitions: List<MeasureRowDef>,

    )



    private data class MeasureRowDef(

        val label: String,

        val extract: (MesureEntry, String) -> RecapTableCell,

    )



    private fun measureGroups(context: Context): List<MeasureGroup> {

        val bpm = context.getString(R.string.recap_unit_bpm)

        val pct = context.getString(R.string.recap_unit_percent)

        val mmHg = context.getString(R.string.recap_unit_mmhg)



        return listOf(

            MeasureGroup(

                subtitle = context.getString(R.string.subblock_respiration),

                definitions = listOf(

                    MeasureRowDef(context.getString(R.string.recap_freq_resp)) { e, empty ->

                        numericCell(e.respiration.frequenceBpm, bpm, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_sat_o2)) { e, empty ->

                        numericCell(e.respiration.saturationPct, pct, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_resp_amplitude)) { e, empty ->

                        choiceCell(e.respiration.amplitude, MesureChoices.respirationAmplitude, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_resp_regularite)) { e, empty ->

                        choiceCell(e.respiration.regularite, MesureChoices.respirationRegularite, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_resp_aspect)) { e, empty ->

                        choiceCell(e.respiration.aspect, MesureChoices.respirationAspect, empty)

                    },

                ),

            ),

            MeasureGroup(

                subtitle = context.getString(R.string.subblock_circulation),

                definitions = listOf(

                    MeasureRowDef(context.getString(R.string.recap_freq_card)) { e, empty ->

                        numericCell(e.circulation.withMigratedTension().frequenceBpm, bpm, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_tension)) { e, empty ->

                        tensionCell(e.circulation.withMigratedTension(), mmHg, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_circ_amplitude)) { e, empty ->

                        choiceCell(e.circulation.amplitude, MesureChoices.circulationAmplitude, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_circ_regularite)) { e, empty ->

                        choiceCell(e.circulation.regularite, MesureChoices.circulationRegularite, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_circ_aspect)) { e, empty ->

                        choiceCell(e.circulation.aspect, MesureChoices.circulationAspect, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_trc)) { e, empty ->

                        choiceCell(e.circulation.trc, MesureChoices.trc, empty)

                    },

                ),

            ),

            MeasureGroup(

                subtitle = context.getString(R.string.subblock_conscience),

                definitions = listOf(

                    MeasureRowDef(context.getString(R.string.recap_conscience)) { e, empty ->

                        choiceCell(e.conscience.conscience, MesureChoices.conscience, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_orient_temps)) { e, empty ->

                        choiceCell(e.conscience.orientationTemps, MesureChoices.ouiNon, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_orient_espace)) { e, empty ->

                        choiceCell(e.conscience.orientationEspace, MesureChoices.ouiNon, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_propos)) { e, empty ->

                        choiceCell(e.conscience.propos, MesureChoices.propos, empty)

                    },

                    MeasureRowDef("Température") { e, empty ->
                        temperatureCell(e.conscience.temperatureC, empty)
                    },

                    MeasureRowDef("Glycémie") { e, empty ->
                        glycemieCell(e.conscience.glycemie, e.conscience.glycemieUnit, empty)
                    },

                ),

            ),

            MeasureGroup(

                subtitle = context.getString(R.string.subblock_glasgow),

                definitions = listOf(

                    MeasureRowDef(context.getString(R.string.recap_glasgow_total)) { e, empty ->

                        glasgowTotalCell(e.glasgow, empty)

                    },

                    MeasureRowDef(context.getString(R.string.glasgow_ouverture_yeux)) { e, empty ->

                        glasgowCell(context, e.glasgow.ouvertureYeux, GlasgowChoices.ouvertureYeux, empty)

                    },

                    MeasureRowDef(context.getString(R.string.glasgow_reponse_verbale)) { e, empty ->

                        glasgowCell(context, e.glasgow.reponseVerbale, GlasgowChoices.reponseVerbale, empty)

                    },

                    MeasureRowDef(context.getString(R.string.glasgow_reponse_motrice)) { e, empty ->

                        glasgowCell(context, e.glasgow.reponseMotrice, GlasgowChoices.reponseMotrice, empty)

                    },

                ),

            ),

            MeasureGroup(

                subtitle = context.getString(R.string.subblock_suspicion_avc),

                definitions = listOf(

                    MeasureRowDef(context.getString(R.string.recap_avc_visage)) { e, empty ->

                        choiceCell(e.suspicionAvc.visage, MesureChoices.visage, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_avc_pupilles_egales)) { e, empty ->

                        choiceCell(e.suspicionAvc.pupillesEgales, MesureChoices.ouiNon, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_avc_pupilles_reactives)) { e, empty ->

                        choiceCell(e.suspicionAvc.pupillesReactives, MesureChoices.ouiNon, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_avc_motricite)) { e, empty ->

                        choiceCell(e.suspicionAvc.motriciteBras, MesureChoices.motricite, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_avc_parole)) { e, empty ->

                        choiceCell(e.suspicionAvc.parole, MesureChoices.parole, empty)

                    },

                    MeasureRowDef(context.getString(R.string.recap_heure_symptomes)) { e, empty ->

                        plainCell(e.suspicionAvc.heureSymptomes, empty)

                    },

                ),

            ),

        )

    }



    private fun numericCell(raw: String, unit: String, emptyCell: String): RecapTableCell {

        val digits = raw.trim()

        if (digits.isEmpty()) return RecapTableCell(emptyCell)

        return RecapTableCell("$digits $unit")

    }



    private fun tensionCell(c: CirculationMesure, mmHg: String, emptyCell: String): RecapTableCell {

        val sys = c.tensionSys.trim()

        val dia = c.tensionDia.trim()

        if (sys.isEmpty() && dia.isEmpty()) return RecapTableCell(emptyCell)

        val text = when {

            sys.isNotEmpty() && dia.isNotEmpty() -> "$sys/$dia $mmHg"

            sys.isNotEmpty() -> "$sys $mmHg"

            else -> "$dia $mmHg"

        }

        return RecapTableCell(text)

    }



    private fun plainCell(raw: String, emptyCell: String): RecapTableCell {

        val trimmed = raw.trim()

        if (trimmed.isEmpty()) return RecapTableCell(emptyCell)

        return RecapTableCell(trimmed)

    }



    private fun glasgowTotalCell(glasgow: GlasgowMesure, emptyCell: String): RecapTableCell {
        val total = glasgow.totalScore() ?: return RecapTableCell(emptyCell)
        return RecapTableCell("$total/15")
    }

    private fun glasgowCell(
        context: Context,
        key: String,
        options: List<GlasgowOption>,
        emptyCell: String,
    ): RecapTableCell {
        val trimmed = key.trim()
        if (trimmed.isEmpty()) return RecapTableCell(emptyCell)
        val option = GlasgowChoices.optionFor(trimmed, options)
            ?: return RecapTableCell(trimmed)
        val label = context.getString(option.labelRes)
        return RecapTableCell(
            context.getString(R.string.glasgow_option_format, option.score, label),
        )
    }

    private fun choiceCell(key: String, options: List<ChoiceOption>, emptyCell: String): RecapTableCell {

        val trimmed = key.trim()

        if (trimmed.isEmpty()) return RecapTableCell(emptyCell)

        val option = options.find { it.key == trimmed }

        return RecapTableCell(

            text = option?.label ?: trimmed,

            tone = option?.tone?.toRecapCellTone() ?: RecapCellTone.None,

        )

    }

    private fun temperatureCell(raw: String, emptyCell: String): RecapTableCell {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return RecapTableCell(emptyCell)
        val value = trimmed.replace(',', '.').toDoubleOrNull()
            ?: return RecapTableCell(trimmed)
        val tone = if (value >= 37.0 && value < 38.0) RecapCellTone.Green else RecapCellTone.Orange
        return RecapTableCell(text = trimmed, tone = tone)
    }

    private fun glycemieCell(raw: String, unit: GlycemieUnit, emptyCell: String): RecapTableCell {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return RecapTableCell(emptyCell)
        val label = when (unit) {
            GlycemieUnit.MgDl -> "mg/dL"
            GlycemieUnit.MmolL -> "mmol/L"
            GlycemieUnit.GL -> "g/L"
        }
        return RecapTableCell("$trimmed $label")
    }

}


