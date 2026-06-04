package com.notesdusecouriste.feature.interventionnotes.ui.recap

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.ViewColumn
import androidx.compose.material.icons.outlined.WidthNormal
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.feature.interventionnotes.R

private val RecapBodyFontSize = 22.sp
private val RecapLabelFontSize = 13.sp
private val RecapQuestionnaireTitleFontSize = 20.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterventionRecapScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    viewModel: InterventionRecapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val recap = uiState.recap
    val measuresTable = recap.measuresTable
    val tableScrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.recap_title),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = uiState.headerTitle,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.recap_back),
                        )
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                ),
            )
        },
    ) { padding ->
        if (recap.isEmpty) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.recap_empty),
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = RecapBodyFontSize),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp),
                    shape = MaterialTheme.shapes.large,
                ) {
                    Text(
                        text = stringResource(R.string.recap_complete_action),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            ) {
                if (recap.identityLines.isNotEmpty()) {
                    stickyHeader(key = "sticky_victime") {
                        RecapStickySectionTitle(
                            text = stringResource(R.string.recap_section_victime),
                        )
                    }
                    item(key = "victime_body") {
                        RecapVictimeCard(
                            lines = recap.identityLines,
                            modifier = Modifier.padding(bottom = 20.dp),
                        )
                    }
                }

                if (measuresTable != null) {
                    stickyHeader(key = "sticky_mesures_title") {
                        RecapMesuresSectionTitle(
                            columnsExpanded = uiState.mesuresColumnsExpanded,
                            onToggleColumnsExpanded = viewModel::toggleMesuresColumnsExpanded,
                        )
                    }
                    stickyHeader(key = "sticky_mesures_header") {
                        RecapMeasuresTableHeaderRow(
                            table = measuresTable,
                            horizontalScrollState = tableScrollState,
                            columnsExpanded = uiState.mesuresColumnsExpanded,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                    item(key = "mesures_body") {
                        RecapMeasuresTableBody(
                            table = measuresTable,
                            horizontalScrollState = tableScrollState,
                            columnsExpanded = uiState.mesuresColumnsExpanded,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                        )
                    }
                }

                if (recap.questionnaires.isNotEmpty()) {
                    stickyHeader(key = "sticky_questionnaires") {
                        RecapStickySectionTitle(
                            text = stringResource(R.string.recap_section_questionnaires),
                        )
                    }
                    item(key = "questionnaires_body") {
                        RecapQuestionnairesCard(
                            sections = recap.questionnaires,
                            modifier = Modifier.padding(bottom = 20.dp),
                        )
                    }
                }

                recap.comment?.let { comment ->
                    stickyHeader(key = "sticky_commentaire") {
                        RecapStickySectionTitle(
                            text = stringResource(R.string.block_commentaire),
                        )
                    }
                    item(key = "commentaire_body") {
                        RecapCommentCard(comment = comment)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecapStickySectionTitle(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(vertical = 8.dp),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
private fun RecapMesuresSectionTitle(
    columnsExpanded: Boolean,
    onToggleColumnsExpanded: () -> Unit,
) {
    val expandDescription = stringResource(
        if (columnsExpanded) {
            R.string.recap_collapse_time_columns
        } else {
            R.string.recap_expand_time_columns
        },
    )
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(R.string.block_mesures),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            IconButton(onClick = onToggleColumnsExpanded) {
                Icon(
                    imageVector = if (columnsExpanded) {
                        Icons.Outlined.WidthNormal
                    } else {
                        Icons.Outlined.ViewColumn
                    },
                    contentDescription = expandDescription,
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun RecapVictimeCard(
    lines: List<RecapLine>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        lines.forEach { line ->
            RecapLineRow(line = line, compactLabel = true)
        }
    }
}

@Composable
private fun RecapCommentCard(comment: String) {
    Text(
        text = comment,
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.bodyLarge.copy(fontSize = RecapBodyFontSize),
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun RecapQuestionnairesCard(
    sections: List<RecapSubSection>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        sections.forEach { section ->
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = section.title,
                    modifier = Modifier.padding(top = 8.dp),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontSize = RecapQuestionnaireTitleFontSize,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                section.lines.forEach { line ->
                    RecapLineRow(line = line, compactLabel = true, tightLabelSpacing = true)
                }
            }
        }
    }
}

@Composable
private fun RecapLineRow(
    line: RecapLine,
    compactLabel: Boolean = false,
    tightLabelSpacing: Boolean = false,
) {
    val labelStyle = if (compactLabel) {
        MaterialTheme.typography.labelMedium.copy(fontSize = RecapLabelFontSize)
    } else {
        MaterialTheme.typography.labelLarge
    }
    val labelGap = if (tightLabelSpacing) 2.dp else 4.dp
    Column(verticalArrangement = Arrangement.spacedBy(labelGap)) {
        Text(
            text = line.label,
            style = labelStyle,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = line.value,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = RecapBodyFontSize),
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
