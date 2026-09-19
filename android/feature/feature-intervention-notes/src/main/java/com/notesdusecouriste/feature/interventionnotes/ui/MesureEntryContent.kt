package com.notesdusecouriste.feature.interventionnotes.ui

import android.content.Context
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.core.data.model.MesureEntry
import com.notesdusecouriste.core.data.model.GlycemieUnit
import com.notesdusecouriste.core.ui.theme.LocalNotesSemanticColors
import com.notesdusecouriste.feature.interventionnotes.R
import com.notesdusecouriste.feature.interventionnotes.ui.components.BoldLabelField
import com.notesdusecouriste.feature.interventionnotes.ui.components.ChoiceChipGroup
import com.notesdusecouriste.feature.interventionnotes.ui.components.GlasgowMesureSection
import com.notesdusecouriste.feature.interventionnotes.ui.components.TensionArterielleFields
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionCard
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteStickyHeaderRegistry
import com.notesdusecouriste.feature.interventionnotes.ui.components.noteMesureSubSectionHeader
import com.notesdusecouriste.feature.interventionnotes.ui.components.noteSectionItem
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val mesureHeureFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.FRANCE)

fun LazyListScope.mesureEntryLazySections(
    entry: MesureEntry,
    viewModel: InterventionNotesViewModel,
    context: Context,
    registry: NoteStickyHeaderRegistry,
) {
    val entryKey = entry.id
    fun isExpanded(kind: NoteSectionKind): Boolean = viewModel.isMesureSubSectionExpanded(kind)
    fun toggle(kind: NoteSectionKind) = viewModel.toggleMesureSubSection(kind)

    noteMesureSubSectionHeader(
        key = "header_resp_$entryKey",
        title = context.getString(R.string.subblock_respiration),
        kind = NoteSectionKind.Respiration,
        registry = registry,
        expanded = isExpanded(NoteSectionKind.Respiration),
        onToggleExpanded = { toggle(NoteSectionKind.Respiration) },
    )
    if (isExpanded(NoteSectionKind.Respiration)) {
        noteSectionItem(key = "resp_$entryKey", registry = registry) {
            NoteSectionCard {
                val r = entry.respiration
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    BoldLabelField(
                        label = context.getString(R.string.field_frequence_respiratoire),
                        value = r.frequenceBpm,
                        onValueChange = { v ->
                            viewModel.updateRespiration(entry.id) { it.copy(frequenceBpm = v) }
                        },
                        suffix = "bpm",
                        maxDigits = 3,
                    )
                    ChoiceChipGroup(
                        label = context.getString(R.string.field_amplitude),
                        options = MesureChoices.respirationAmplitude,
                        selectedKey = r.amplitude,
                        onSelect = { key ->
                            viewModel.updateRespiration(entry.id) { it.copy(amplitude = key) }
                        },
                    )
                    ChoiceChipGroup(
                        label = context.getString(R.string.field_regularite),
                        options = MesureChoices.respirationRegularite,
                        selectedKey = r.regularite,
                        onSelect = { key ->
                            viewModel.updateRespiration(entry.id) { it.copy(regularite = key) }
                        },
                    )
                    ChoiceChipGroup(
                        label = context.getString(R.string.field_aspect),
                        options = MesureChoices.respirationAspect,
                        selectedKey = r.aspect,
                        onSelect = { key ->
                            viewModel.updateRespiration(entry.id) { it.copy(aspect = key) }
                        },
                    )
                    BoldLabelField(
                        label = context.getString(R.string.field_saturation_o2),
                        value = r.saturationPct,
                        onValueChange = { v ->
                            viewModel.updateRespiration(entry.id) { it.copy(saturationPct = v) }
                        },
                        suffix = "%",
                        maxDigits = 3,
                    )
                }
            }
        }
    }

    noteMesureSubSectionHeader(
        key = "header_circ_$entryKey",
        title = context.getString(R.string.subblock_circulation),
        kind = NoteSectionKind.Circulation,
        registry = registry,
        expanded = isExpanded(NoteSectionKind.Circulation),
        onToggleExpanded = { toggle(NoteSectionKind.Circulation) },
    )
    if (isExpanded(NoteSectionKind.Circulation)) {
        noteSectionItem(key = "circ_$entryKey", registry = registry) {
            NoteSectionCard {
                val c = entry.circulation
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                BoldLabelField(
                    label = context.getString(R.string.field_frequence_cardiaque),
                    value = c.frequenceBpm,
                    onValueChange = { v ->
                        viewModel.updateCirculation(entry.id) { it.copy(frequenceBpm = v) }
                    },
                    suffix = "bpm",
                    maxDigits = 3,
                )
                ChoiceChipGroup(
                    label = context.getString(R.string.field_amplitude),
                    options = MesureChoices.circulationAmplitude,
                    selectedKey = c.amplitude,
                    onSelect = { key ->
                        viewModel.updateCirculation(entry.id) { it.copy(amplitude = key) }
                    },
                )
                ChoiceChipGroup(
                    label = context.getString(R.string.field_regularite),
                    options = MesureChoices.circulationRegularite,
                    selectedKey = c.regularite,
                    onSelect = { key ->
                        viewModel.updateCirculation(entry.id) { it.copy(regularite = key) }
                    },
                )
                ChoiceChipGroup(
                    label = context.getString(R.string.field_aspect),
                    options = MesureChoices.circulationAspect,
                    selectedKey = c.aspect,
                    onSelect = { key ->
                        viewModel.updateCirculation(entry.id) { it.copy(aspect = key) }
                    },
                )
                TensionArterielleFields(
                    tensionSys = c.tensionSys,
                    tensionDia = c.tensionDia,
                    onTensionSysChange = { v ->
                        viewModel.updateCirculation(entry.id) { it.copy(tensionSys = v) }
                    },
                    onTensionDiaChange = { v ->
                        viewModel.updateCirculation(entry.id) { it.copy(tensionDia = v) }
                    },
                )
                ChoiceChipGroup(
                    label = context.getString(R.string.field_trc),
                    options = MesureChoices.trc,
                    selectedKey = c.trc,
                    onSelect = { key ->
                        viewModel.updateCirculation(entry.id) { it.copy(trc = key) }
                    },
                )
                }
            }
        }
    }

    noteMesureSubSectionHeader(
        key = "header_cons_$entryKey",
        title = context.getString(R.string.subblock_conscience),
        kind = NoteSectionKind.Conscience,
        registry = registry,
        expanded = isExpanded(NoteSectionKind.Conscience),
        onToggleExpanded = { toggle(NoteSectionKind.Conscience) },
    )
    if (isExpanded(NoteSectionKind.Conscience)) {
        noteSectionItem(key = "cons_$entryKey", registry = registry) {
            NoteSectionCard {
                val c = entry.conscience
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ChoiceChipGroup(
                    label = "Conscience",
                    options = MesureChoices.conscience,
                    selectedKey = c.conscience,
                    boldFirstLetter = false,
                    onSelect = { key ->
                        viewModel.updateConscience(entry.id) { it.copy(conscience = key) }
                    },
                )
                ChoiceChipGroup(
                    label = "Orientation dans le temps",
                    options = MesureChoices.ouiNon,
                    selectedKey = c.orientationTemps,
                    boldFirstLetter = false,
                    onSelect = { key ->
                        viewModel.updateConscience(entry.id) { it.copy(orientationTemps = key) }
                    },
                )
                ChoiceChipGroup(
                    label = "Orientation dans l'espace",
                    options = MesureChoices.ouiNon,
                    selectedKey = c.orientationEspace,
                    boldFirstLetter = false,
                    onSelect = { key ->
                        viewModel.updateConscience(entry.id) { it.copy(orientationEspace = key) }
                    },
                )
                ChoiceChipGroup(
                    label = "Propos",
                    options = MesureChoices.propos,
                    selectedKey = c.propos,
                    boldFirstLetter = false,
                    onSelect = { key ->
                        viewModel.updateConscience(entry.id) { it.copy(propos = key) }
                    },
                )

                TemperatureField(
                    value = c.temperatureC,
                    onValueChange = { v ->
                        viewModel.updateConscience(entry.id) { it.copy(temperatureC = v) }
                    },
                )

                GlycemieField(
                    value = c.glycemie,
                    unit = c.glycemieUnit,
                    onValueChange = { v ->
                        viewModel.updateConscience(entry.id) { it.copy(glycemie = v) }
                    },
                    onUnitChange = { unit ->
                        viewModel.updateConscience(entry.id) { it.copy(glycemieUnit = unit) }
                    },
                )

                // Glasgow déplacé ici (fin du sous-chapitre Conscience).
                val g = entry.glasgow
                GlasgowMesureSection(
                    glasgow = g,
                    onOuvertureYeuxChange = { key ->
                        viewModel.updateGlasgow(entry.id) { it.copy(ouvertureYeux = key) }
                    },
                    onReponseVerbaleChange = { key ->
                        viewModel.updateGlasgow(entry.id) { it.copy(reponseVerbale = key) }
                    },
                    onReponseMotriceChange = { key ->
                        viewModel.updateGlasgow(entry.id) { it.copy(reponseMotrice = key) }
                    },
                )
                }
            }
        }
    }

    noteMesureSubSectionHeader(
        key = "header_avc_$entryKey",
        title = context.getString(R.string.subblock_suspicion_avc),
        kind = NoteSectionKind.SuspicionAvc,
        registry = registry,
        expanded = isExpanded(NoteSectionKind.SuspicionAvc),
        onToggleExpanded = { toggle(NoteSectionKind.SuspicionAvc) },
    )
    if (isExpanded(NoteSectionKind.SuspicionAvc)) {
        noteSectionItem(key = "avc_$entryKey", registry = registry) {
            NoteSectionCard {
                val a = entry.suspicionAvc
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ChoiceChipGroup(
                    label = "Visage",
                    options = MesureChoices.visage,
                    selectedKey = a.visage,
                    boldFirstLetter = false,
                    onSelect = { key ->
                        viewModel.updateSuspicionAvc(entry.id) { it.copy(visage = key) }
                    },
                )
                ChoiceChipGroup(
                    label = "Pupilles égales",
                    options = MesureChoices.ouiNon,
                    selectedKey = a.pupillesEgales,
                    boldFirstLetter = false,
                    onSelect = { key ->
                        viewModel.updateSuspicionAvc(entry.id) { it.copy(pupillesEgales = key) }
                    },
                )
                ChoiceChipGroup(
                    label = "Pupilles réactives à la lumière",
                    options = MesureChoices.ouiNon,
                    selectedKey = a.pupillesReactives,
                    boldFirstLetter = false,
                    onSelect = { key ->
                        viewModel.updateSuspicionAvc(entry.id) { it.copy(pupillesReactives = key) }
                    },
                )
                ChoiceChipGroup(
                    label = "Motricité des bras",
                    options = MesureChoices.motricite,
                    selectedKey = a.motriciteBras,
                    boldFirstLetter = false,
                    onSelect = { key ->
                        viewModel.updateSuspicionAvc(entry.id) { it.copy(motriciteBras = key) }
                    },
                )
                ChoiceChipGroup(
                    label = "Parole",
                    options = MesureChoices.parole,
                    selectedKey = a.parole,
                    boldFirstLetter = false,
                    onSelect = { key ->
                        viewModel.updateSuspicionAvc(entry.id) { it.copy(parole = key) }
                    },
                )
                }
            }
        }
    }
}

fun formatMesureHeure(epochMillis: Long): String {
    if (epochMillis <= 0L) return ""
    return Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(mesureHeureFormatter)
}

fun parseHeureToEpoch(hhmm: String): Long? {
    val trimmed = hhmm.trim()
    if (trimmed.isEmpty()) return null
    return runCatching {
        val time = LocalTime.parse(trimmed, mesureHeureFormatter)
        LocalDateTime.of(LocalDate.now(), time)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }.getOrNull()
}

private fun isTemperatureInputValid(value: String): Boolean {
    if (value.isBlank()) return true
    val trimmed = value.trim()
    return Regex("^\\d{0,2}(,\\d{0,2})?$").matches(trimmed)
}

private fun parseTemperatureC(value: String): Double? =
    value.trim()
        .takeIf { it.isNotEmpty() }
        ?.replace(',', '.')
        ?.toDoubleOrNull()

@Composable
private fun TemperatureField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    val valid = isTemperatureInputValid(value)
    val parsed = parseTemperatureC(value)
    val isNormal = parsed?.let { it >= 37.0 && it < 38.0 }
    val semantic = LocalNotesSemanticColors.current

    val borderColor: Color? = when {
        value.isBlank() || !valid || parsed == null -> null
        isNormal == true -> semantic.normalOnContainer
        else -> semantic.alertOnContainer
    }

    val colors = if (borderColor != null) {
        OutlinedTextFieldDefaults.colors(
            focusedBorderColor = borderColor,
            unfocusedBorderColor = borderColor,
            focusedLabelColor = borderColor,
            unfocusedLabelColor = borderColor,
            focusedSuffixColor = borderColor,
            unfocusedSuffixColor = borderColor,
            cursorColor = borderColor,
        )
    } else {
        OutlinedTextFieldDefaults.colors()
    }

    OutlinedTextField(
        value = value,
        onValueChange = { raw ->
            val filtered = raw
                .filter { it.isDigit() || it == ',' || it == '.' }
                .replace('.', ',')
            val parts = filtered.split(',', limit = 2)
            val intPart = parts.getOrElse(0) { "" }.take(2)
            val decPart = parts.getOrElse(1) { "" }.take(2)
            val normalized = if (filtered.contains(',')) "$intPart,$decPart" else intPart
            onValueChange(normalized)
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("Température") },
        suffix = { Text("°C") },
        singleLine = true,
        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = value.isNotEmpty() && !valid,
        colors = colors,
        textStyle = MaterialTheme.typography.bodyLarge,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GlycemieField(
    value: String,
    unit: GlycemieUnit,
    onValueChange: (String) -> Unit,
    onUnitChange: (GlycemieUnit) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val unitLabel = when (unit) {
        GlycemieUnit.MgDl -> "mg/dL"
        GlycemieUnit.MmolL -> "mmol/L"
        GlycemieUnit.GL -> "g/L"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        BoldLabelField(
            label = "Glycémie",
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.weight(1f),
            maxDigits = 5,
            keyboardType = KeyboardType.Number,
            boldFirstLetter = false,
        )

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier.weight(0.55f),
        ) {
            OutlinedTextField(
                value = unitLabel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Unité") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
            )
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                listOf(GlycemieUnit.MgDl, GlycemieUnit.MmolL, GlycemieUnit.GL).forEach { opt ->
                    val label = when (opt) {
                        GlycemieUnit.MgDl -> "mg/dL"
                        GlycemieUnit.MmolL -> "mmol/L"
                        GlycemieUnit.GL -> "g/L"
                    }
                    DropdownMenuItem(
                        text = { Text(label, style = MaterialTheme.typography.bodyLarge) },
                        onClick = {
                            onUnitChange(opt)
                            expanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }
        }
    }
}
