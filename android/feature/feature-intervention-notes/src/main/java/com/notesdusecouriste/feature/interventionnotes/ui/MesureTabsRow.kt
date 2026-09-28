package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.core.data.model.MesureEntry
import com.notesdusecouriste.core.ui.components.PrimaryAddTab
import com.notesdusecouriste.core.ui.components.PrimaryLongPressTab
import com.notesdusecouriste.feature.interventionnotes.R

/** Onglets des relevés — « Primary tabs » Material 3, avec onglet « + » plein. */
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

    ScrollableTabRow(
        selectedTabIndex = safeSelected,
        modifier = modifier.fillMaxWidth(),
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        contentColor = MaterialTheme.colorScheme.onSurface,
        edgePadding = 8.dp,
        divider = {},
        indicator = { tabPositions ->
            if (entries.isNotEmpty()) {
                val position = tabPositions[safeSelected]
                TabRowDefaults.PrimaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(position),
                    width = position.contentWidth,
                    shape = RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp),
                )
            }
        },
    ) {
        entries.forEachIndexed { index, entry ->
            PrimaryLongPressTab(
                title = formatMesureTabLabel(entry.horodatageEpochMillis, showDateOnTabs),
                selected = index == safeSelected,
                onClick = { onSelectTab(index) },
                onLongClick = { onTabLongPress(entry) },
            )
        }
        PrimaryAddTab(
            onClick = onAddTab,
            contentDescription = stringResource(R.string.mesures_add_tab),
        )
    }
}
