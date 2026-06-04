package com.notesdusecouriste.app.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.app.R
import com.notesdusecouriste.core.data.model.Intervention
import com.notesdusecouriste.core.data.model.formatHomeIdentityLine
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ListDividerInsetDefault = 16.dp
private val ListDividerInsetSelection = 72.dp

private val listDateFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm", Locale.FRANCE)

/**
 * Liste groupée d’interventions — [Material 3 Lists](https://m3.material.io/components/lists/specs).
 */
@Composable
fun InterventionHistoryList(
    interventions: List<Intervention>,
    isSelectionMode: Boolean,
    selectedIds: Set<Long>,
    onOpen: (Long) -> Unit,
    onOpenRecap: (Long) -> Unit,
    onLongPress: (Long) -> Unit,
    onToggleSelection: (Long) -> Unit,
    onDeleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dividerInset = if (isSelectionMode) ListDividerInsetSelection else ListDividerInsetDefault

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column {
            interventions.forEachIndexed { index, intervention ->
                val selected = intervention.id in selectedIds
                InterventionListItem(
                    intervention = intervention,
                    isSelectionMode = isSelectionMode,
                    isSelected = selected,
                    onOpen = { onOpen(intervention.id) },
                    onOpenRecap = { onOpenRecap(intervention.id) },
                    onLongPress = { onLongPress(intervention.id) },
                    onToggleSelection = { onToggleSelection(intervention.id) },
                    onDeleteClick = { onDeleteClick(intervention.id) },
                )
                if (index < interventions.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(start = dividerInset),
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InterventionListItem(
    intervention: Intervention,
    onOpen: () -> Unit,
    onOpenRecap: () -> Unit,
    onLongPress: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelectionMode: Boolean = false,
    isSelected: Boolean = false,
    onToggleSelection: () -> Unit = {},
) {
    val recapDescription = stringResource(R.string.content_description_open_recap)
    val deleteDescription = stringResource(R.string.content_description_delete_intervention)

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) onToggleSelection() else onOpen()
                },
                onLongClick = {
                    if (!isSelectionMode) onLongPress()
                },
            ),
        colors = ListItemDefaults.colors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.secondaryContainer
                else -> Color.Transparent
            },
        ),
        headlineContent = {
            Text(
                text = formatIdentityLine(intervention),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        },
        supportingContent = {
            Text(
                text = formatStartedAt(intervention.startedAtEpochMillis),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingContent = if (isSelectionMode) {
            {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelection() },
                )
            }
        } else {
            null
        },
        trailingContent = if (!isSelectionMode) {
            {
                Row {
                    IconButton(
                        onClick = onOpenRecap,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Summarize,
                            contentDescription = recapDescription,
                            tint = MaterialTheme.colorScheme.secondary,
                        )
                    }
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = deleteDescription,
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        } else {
            null
        },
    )
}

private fun formatIdentityLine(intervention: Intervention): String =
    formatHomeIdentityLine(
        nom = intervention.nom,
        prenom = intervention.prenom,
        age = intervention.age,
    ).let { line ->
        if (line == "Nouvelle note") {
            "Intervention en cours"
        } else {
            line
        }
    }

private fun formatStartedAt(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(listDateFormatter)
