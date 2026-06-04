package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.core.data.model.MesureEntry
import com.notesdusecouriste.feature.interventionnotes.R
import com.notesdusecouriste.feature.interventionnotes.ui.components.AddTabIcon

private val TabRowHeight = 48.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MesureTabsRow(
    entries: List<MesureEntry>,
    selectedIndex: Int,
    onSelectTab: (Int) -> Unit,
    onAddTab: () -> Unit,
    onTabLongPress: (MesureEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    val safeSelected = selectedIndex.coerceIn(0, (entries.size - 1).coerceAtLeast(0))
    val showDateOnTabs = mesureTabLabelsShowDate(entries)
    val scrollState = rememberScrollState()
    val indicatorHeightPx = with(LocalDensity.current) { 3.dp.toPx() }
    val primaryColor = MaterialTheme.colorScheme.primary

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState)
                .height(TabRowHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            entries.forEachIndexed { index, entry ->
                val selected = index == safeSelected
                val interactionSource = remember(entry.id) { MutableInteractionSource() }
                Box(
                    modifier = Modifier
                        .height(TabRowHeight)
                        .then(
                            if (selected) {
                                Modifier.drawBehind {
                                    drawLine(
                                        color = primaryColor,
                                        start = Offset(0f, size.height - indicatorHeightPx),
                                        end = Offset(size.width, size.height - indicatorHeightPx),
                                        strokeWidth = indicatorHeightPx,
                                    )
                                }
                            } else {
                                Modifier
                            },
                        )
                        .combinedClickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.Tab,
                            onClick = { onSelectTab(index) },
                            onLongClick = { onTabLongPress(entry) },
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = formatMesureTabLabel(entry.horodatageEpochMillis, showDateOnTabs),
                        style = MaterialTheme.typography.labelLarge,
                        color = if (selected) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }
            IconButton(onClick = onAddTab) {
                Icon(
                    imageVector = AddTabIcon,
                    contentDescription = stringResource(R.string.mesures_add_tab),
                )
            }
        }
    }
}
