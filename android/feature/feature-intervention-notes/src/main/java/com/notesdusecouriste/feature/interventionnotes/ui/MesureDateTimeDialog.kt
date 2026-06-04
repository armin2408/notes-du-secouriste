package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.notesdusecouriste.core.data.model.dateNaissanceDigitsOnly
import com.notesdusecouriste.core.data.model.filterFrenchTimeDigitsInput
import com.notesdusecouriste.core.data.model.isFrenchDateInputValid
import com.notesdusecouriste.core.data.model.isFrenchTimeInputValid
import com.notesdusecouriste.core.data.model.parseFrenchDateDigits
import com.notesdusecouriste.core.data.model.parseFrenchTimeDigits
import com.notesdusecouriste.core.data.model.MesureEntry
import com.notesdusecouriste.core.data.model.timeDigitsOnly
import com.notesdusecouriste.feature.interventionnotes.R
import com.notesdusecouriste.feature.interventionnotes.ui.components.BoldLabelField
import com.notesdusecouriste.feature.interventionnotes.ui.components.DateNaissanceVisualTransformation
import com.notesdusecouriste.feature.interventionnotes.ui.components.TimeVisualTransformation
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

enum class MesureDateTimeEditMode {
    DateOnly,
    TimeOnly,
    DateAndTime,
}

private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRANCE)
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.FRANCE)
private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM HH:mm", Locale.FRANCE)

/** Affiche la date sur les onglets seulement si les relevés de la note ont des jours différents. */
fun mesureTabLabelsShowDate(entries: List<MesureEntry>): Boolean =
    mesureTabLabelsShowDate(entries.asSequence().map { it.horodatageEpochMillis })

private fun mesureTabLabelsShowDate(epochMillisList: Sequence<Long>): Boolean {
    val dates = epochMillisList
        .filter { it > 0L }
        .map { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate() }
        .toSet()
    return dates.size > 1
}

@Composable
fun MesureDateTimeDialog(
    initialEpochMillis: Long,
    mode: MesureDateTimeEditMode,
    onDismiss: () -> Unit,
    onConfirm: (Long) -> Unit,
) {
    val zone = ZoneId.systemDefault()
    val initial = Instant.ofEpochMilli(
        initialEpochMillis.takeIf { it > 0L } ?: System.currentTimeMillis(),
    ).atZone(zone)
    var dateDigits by remember(mode, initialEpochMillis) {
        mutableStateOf(epochToDateDigits(initialEpochMillis))
    }
    var timeDigits by remember(mode, initialEpochMillis) {
        mutableStateOf(epochToTimeDigits(initialEpochMillis))
    }

    val title = when (mode) {
        MesureDateTimeEditMode.DateOnly -> stringResource(R.string.mesures_edit_date_title)
        MesureDateTimeEditMode.TimeOnly -> stringResource(R.string.mesures_edit_time_title)
        MesureDateTimeEditMode.DateAndTime -> stringResource(R.string.mesures_edit_datetime_title)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (mode == MesureDateTimeEditMode.DateAndTime) {
                    Text(
                        text = stringResource(R.string.mesures_edit_datetime_hint),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                if (mode != MesureDateTimeEditMode.TimeOnly) {
                    BoldLabelField(
                        label = stringResource(R.string.mesures_edit_date),
                        value = dateDigits,
                        onValueChange = { dateDigits = dateNaissanceDigitsOnly(it) },
                        keyboardType = KeyboardType.Number,
                        boldFirstLetter = false,
                        visualTransformation = DateNaissanceVisualTransformation(),
                        isInputValid = isFrenchDateInputValid(dateDigits),
                    )
                }
                if (mode != MesureDateTimeEditMode.DateOnly) {
                    BoldLabelField(
                        label = stringResource(R.string.mesures_edit_time),
                        value = timeDigits,
                        onValueChange = { timeDigits = filterFrenchTimeDigitsInput(it) },
                        keyboardType = KeyboardType.Number,
                        boldFirstLetter = false,
                        visualTransformation = TimeVisualTransformation(),
                        isInputValid = isFrenchTimeInputValid(timeDigits),
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val epoch = when (mode) {
                        MesureDateTimeEditMode.DateOnly ->
                            mergeDate(initialEpochMillis, dateDigits)
                        MesureDateTimeEditMode.TimeOnly ->
                            mergeTime(initialEpochMillis, timeDigits)
                        MesureDateTimeEditMode.DateAndTime ->
                            parseDateTimeToEpoch(dateDigits, timeDigits)
                    }
                    epoch?.let(onConfirm)
                    onDismiss()
                },
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

@Composable
fun MesureDeleteConfirmDialog(
    tabLabel: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.mesures_delete_title),
                style = MaterialTheme.typography.headlineSmall,
            )
        },
        text = {
            Text(
                text = stringResource(R.string.mesures_delete_message, tabLabel),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.mesures_delete_confirm),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}

private fun epochToDateDigits(epochMillis: Long): String {
    val zone = ZoneId.systemDefault()
    val instant = Instant.ofEpochMilli(
        epochMillis.takeIf { it > 0L } ?: System.currentTimeMillis(),
    ).atZone(zone)
    val formatted = instant.toLocalDate().format(dateFormatter)
    return dateNaissanceDigitsOnly(formatted)
}

private fun epochToTimeDigits(epochMillis: Long): String {
    val zone = ZoneId.systemDefault()
    val instant = Instant.ofEpochMilli(
        epochMillis.takeIf { it > 0L } ?: System.currentTimeMillis(),
    ).atZone(zone)
    val formatted = instant.toLocalTime().format(timeFormatter)
    return timeDigitsOnly(formatted)
}

fun parseDateTimeToEpoch(dateDigits: String, timeDigits: String): Long? {
    return runCatching {
        val localDate = parseFrenchDateDigits(dateDigits) ?: return null
        val localTime = parseFrenchTimeDigits(timeDigits) ?: return null
        LocalDateTime.of(localDate, localTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }.getOrNull()
}

fun mergeDate(existingEpochMillis: Long, dateDigits: String): Long? {
    val zone = ZoneId.systemDefault()
    val existing = Instant.ofEpochMilli(
        existingEpochMillis.takeIf { it > 0L } ?: System.currentTimeMillis(),
    ).atZone(zone)
    return runCatching {
        val newDate = parseFrenchDateDigits(dateDigits) ?: return null
        LocalDateTime.of(newDate, existing.toLocalTime())
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
    }.getOrNull()
}

fun mergeTime(existingEpochMillis: Long, timeText: String): Long? {
    val zone = ZoneId.systemDefault()
    val existing = Instant.ofEpochMilli(
        existingEpochMillis.takeIf { it > 0L } ?: System.currentTimeMillis(),
    ).atZone(zone)
    return runCatching {
        val newTime = parseFrenchTimeDigits(timeText) ?: return null
        LocalDateTime.of(existing.toLocalDate(), newTime)
            .atZone(zone)
            .toInstant()
            .toEpochMilli()
    }.getOrNull()
}

fun formatMesureTabLabel(epochMillis: Long, showDate: Boolean): String {
    if (epochMillis <= 0L) return "—"
    val zdt = Instant.ofEpochMilli(epochMillis).atZone(ZoneId.systemDefault())
    return if (showDate) {
        zdt.format(dateTimeFormatter)
    } else {
        zdt.format(timeFormatter)
    }
}

fun formatMesureTabLabel(epochMillis: Long, entries: List<MesureEntry>): String =
    formatMesureTabLabel(epochMillis, mesureTabLabelsShowDate(entries))
