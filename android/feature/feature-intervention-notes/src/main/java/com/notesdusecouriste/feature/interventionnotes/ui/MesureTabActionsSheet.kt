package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.core.data.model.MesureEntry
import com.notesdusecouriste.feature.interventionnotes.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesureTabActionsSheet(
    entry: MesureEntry,
    allEntries: List<MesureEntry>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onEditTime: () -> Unit,
    onEditDate: () -> Unit,
    onDelete: () -> Unit,
) {
    val tabLabel = formatMesureTabLabel(entry.horodatageEpochMillis, allEntries)
    val scope = rememberCoroutineScope()

    LaunchedEffect(entry.id) {
        scope.launch { sheetState.show() }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
        ) {
            Text(
                text = stringResource(R.string.mesures_tab_actions_title, tabLabel),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            MesureTabActionRow(
                label = stringResource(R.string.mesures_action_edit_time),
                onClick = onEditTime,
            )
            HorizontalDivider()
            MesureTabActionRow(
                label = stringResource(R.string.mesures_action_edit_date),
                onClick = onEditDate,
            )
            HorizontalDivider()
            MesureTabActionRow(
                label = stringResource(R.string.mesures_action_delete),
                onClick = onDelete,
                isDestructive = true,
            )
        }
    }
}

@Composable
private fun MesureTabActionRow(
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.bodyLarge,
        color = if (isDestructive) {
            MaterialTheme.colorScheme.error
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 24.dp, vertical = 16.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberMesureTabActionsSheetState() = rememberModalBottomSheetState(skipPartiallyExpanded = true)
