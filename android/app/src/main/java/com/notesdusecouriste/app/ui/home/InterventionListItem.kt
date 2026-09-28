package com.notesdusecouriste.app.ui.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PictureAsPdf
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.app.R
import com.notesdusecouriste.core.data.model.Intervention
import com.notesdusecouriste.core.data.model.formatHomeIdentityLine
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val SegmentOuterRadius = 20.dp
private val SegmentInnerRadius = 4.dp
private val SegmentGap = 2.dp

private val listDateFormatter =
    DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm", Locale.FRANCE)

/**
 * Liste segmentée d’interventions — [Material 3 Expressive Lists](https://m3.material.io/components/lists/overview).
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
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SegmentGap),
    ) {
        interventions.forEachIndexed { index, intervention ->
            val selected = intervention.id in selectedIds
            InterventionListItem(
                intervention = intervention,
                isSelectionMode = isSelectionMode,
                isSelected = selected,
                segmentIndex = index,
                segmentCount = interventions.size,
                onOpen = { onOpen(intervention.id) },
                onOpenRecap = { onOpenRecap(intervention.id) },
                onLongPress = { onLongPress(intervention.id) },
                onToggleSelection = { onToggleSelection(intervention.id) },
                onDeleteClick = { onDeleteClick(intervention.id) },
            )
        }
    }
}

@Composable
private fun animateSegmentCorner(rounded: Boolean) = animateDpAsState(
    targetValue = if (rounded) SegmentOuterRadius else SegmentInnerRadius,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    ),
    label = "segmentCorner",
)

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
    segmentIndex: Int = 0,
    segmentCount: Int = 1,
) {
    val recapDescription = stringResource(R.string.content_description_open_recap)
    val deleteDescription = stringResource(R.string.content_description_delete_intervention)
    val canOpenSynthesis = intervention.hasSynthesisContent

    val top by animateSegmentCorner(isSelected || segmentIndex == 0)
    val bottom by animateSegmentCorner(isSelected || segmentIndex == segmentCount - 1)
    val containerColor by animateColorAsState(
        if (isSelected) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer,
        label = "itemContainer",
    )
    val shape = RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)

    ListItem(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .combinedClickable(
                onClick = {
                    if (isSelectionMode) onToggleSelection() else onOpen()
                },
                onLongClick = {
                    if (!isSelectionMode) onLongPress()
                },
            ),
        colors = ListItemDefaults.colors(
            containerColor = containerColor,
        ),
        headlineContent = {
            Text(
                text = formatIdentityLine(intervention),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
            { InterventionAvatar(intervention) }
        },
        trailingContent = if (!isSelectionMode) {
            {
                Row {
                    IconButton(
                        onClick = onOpenRecap,
                        enabled = canOpenSynthesis,
                        modifier = Modifier.size(48.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PictureAsPdf,
                            contentDescription = recapDescription,
                            tint = if (canOpenSynthesis) {
                                MaterialTheme.colorScheme.secondary
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                            },
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

@Composable
private fun InterventionAvatar(intervention: Intervention) {
    val initials = listOfNotNull(intervention.prenom, intervention.nom)
        .mapNotNull { it.trim().firstOrNull()?.uppercaseChar() }
        .joinToString("")
    Surface(
        modifier = Modifier.size(40.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (initials.isNotEmpty()) {
                Text(text = initials, style = MaterialTheme.typography.titleSmall)
            } else {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}

private fun formatIdentityLine(intervention: Intervention): String =
    formatHomeIdentityLine(
        nom = intervention.nom,
        prenom = intervention.prenom,
        age = intervention.age,
    ).let { line ->
        if (line == "Nouvelle note") {
            "Nouvelle intervention"
        } else {
            line
        }
    }

private fun formatStartedAt(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(listDateFormatter)
