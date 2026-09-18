package com.notesdusecouriste.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.app.R
import com.notesdusecouriste.app.ui.components.AideMemoireThemeActions
import com.notesdusecouriste.app.ui.settings.PrivacyGuardrailDialog
import com.notesdusecouriste.app.ui.theme.AppThemeViewModel
import com.notesdusecouriste.app.ui.theme.appThemeViewModel
import com.notesdusecouriste.core.data.model.Intervention
import com.notesdusecouriste.core.data.model.InterventionStatus
import com.notesdusecouriste.core.ui.preview.ResponsivePreviews
import com.notesdusecouriste.core.ui.preview.ThemePreviews
import com.notesdusecouriste.core.ui.systembars.navigationBarBottomPadding
import com.notesdusecouriste.core.ui.systembars.scaffoldContentWithoutNavigationBar
import com.notesdusecouriste.core.ui.theme.NotesDuSecouristeTheme

@Composable
fun HomeScreen(
    onNewIntervention: (Long) -> Unit,
    onAideMemoire: () -> Unit,
    onOpenIntervention: (Long) -> Unit,
    onOpenRecap: (Long) -> Unit,
    onSettings: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    themeViewModel: AppThemeViewModel = appThemeViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val interventions by viewModel.interventions.collectAsStateWithLifecycle()
    val isDark by themeViewModel.effectiveIsDark.collectAsStateWithLifecycle()

    PrivacyGuardrailDialog()

    HomeScreenContent(
        uiState = uiState,
        interventions = interventions,
        isDark = isDark,
        onNewIntervention = { viewModel.createIntervention(onNewIntervention) },
        onAideMemoire = onAideMemoire,
        onToggleTheme = { themeViewModel.toggleTheme(isDark) },
        onSettings = onSettings,
        onOpenIntervention = onOpenIntervention,
        onOpenRecap = onOpenRecap,
        onInterventionLongPress = viewModel::onInterventionLongPress,
        onToggleSelection = viewModel::toggleSelection,
        onRequestDeleteSingle = viewModel::requestDeleteSingle,
        onExitSelectionMode = viewModel::exitSelectionMode,
        onRequestDeleteSelected = viewModel::requestDeleteSelected,
        onConfirmDelete = viewModel::confirmDelete,
        onDismissDeleteConfirm = viewModel::dismissDeleteConfirm,
        onErrorConsumed = viewModel::clearError,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreenContent(
    uiState: HomeUiState,
    interventions: List<Intervention>,
    isDark: Boolean,
    onNewIntervention: () -> Unit,
    onAideMemoire: () -> Unit,
    onToggleTheme: () -> Unit,
    onSettings: () -> Unit,
    onOpenIntervention: (Long) -> Unit,
    onOpenRecap: (Long) -> Unit,
    onInterventionLongPress: (Long) -> Unit,
    onToggleSelection: (Long) -> Unit,
    onRequestDeleteSingle: (Long) -> Unit,
    onExitSelectionMode: () -> Unit,
    onRequestDeleteSelected: () -> Unit,
    onConfirmDelete: () -> Unit,
    onDismissDeleteConfirm: () -> Unit,
    onErrorConsumed: () -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val topBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
    )

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onErrorConsumed()
        }
    }

    uiState.deleteConfirm?.let { request ->
        val message = when (request) {
            is DeleteConfirmRequest.Single -> stringResource(R.string.delete_confirm_single)
            is DeleteConfirmRequest.Multiple ->
                stringResource(R.string.delete_confirm_multiple, request.ids.size)
        }
        AlertDialog(
            onDismissRequest = onDismissDeleteConfirm,
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = onConfirmDelete,
                    enabled = !uiState.isDeleting,
                ) {
                    Text(stringResource(R.string.delete_confirm_action))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissDeleteConfirm,
                    enabled = !uiState.isDeleting,
                ) {
                    Text(stringResource(R.string.delete_cancel))
                }
            },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = scaffoldContentWithoutNavigationBar(),
        topBar = {
            if (uiState.isSelectionMode) {
                TopAppBar(
                    title = {
                        Text(
                            stringResource(
                                R.string.home_selection_count,
                                uiState.selectedIds.size,
                            ),
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onExitSelectionMode) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.home_selection_cancel),
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = onRequestDeleteSelected,
                            enabled = uiState.selectedIds.isNotEmpty() && !uiState.isDeleting,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = stringResource(R.string.home_selection_delete),
                                tint = MaterialTheme.colorScheme.error,
                            )
                        }
                    },
                    colors = topBarColors,
                )
            } else {
                TopAppBar(
                    title = {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Text(
                                    text = stringResource(R.string.app_beta_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                    },
                    actions = {
                        AideMemoireThemeActions(
                            onAideMemoire = onAideMemoire,
                            isDark = isDark,
                            onToggleTheme = onToggleTheme,
                        )
                    },
                    colors = topBarColors,
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                start = 20.dp,
                end = 20.dp,
                top = 16.dp,
                bottom = navigationBarBottomPadding(extra = 16.dp),
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!uiState.isSelectionMode) {
                item {
                    Button(
                        onClick = onNewIntervention,
                        enabled = !uiState.isCreating,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 72.dp),
                        shape = MaterialTheme.shapes.extraLarge,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 2.dp,
                            pressedElevation = 6.dp,
                        ),
                    ) {
                        if (uiState.isCreating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.Add,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.home_new_intervention),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }
                    }
                }
            } else {
                item {
                    Text(
                        text = stringResource(R.string.home_selection_hint),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            if (interventions.isEmpty()) {
                item {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    ) {
                        Text(
                            text = stringResource(R.string.home_list_empty),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                        )
                    }
                }
            } else {
                item(key = "intervention_history_list") {
                    InterventionHistoryList(
                        interventions = interventions,
                        isSelectionMode = uiState.isSelectionMode,
                        selectedIds = uiState.selectedIds,
                        onOpen = onOpenIntervention,
                        onOpenRecap = onOpenRecap,
                        onLongPress = onInterventionLongPress,
                        onToggleSelection = onToggleSelection,
                        onDeleteClick = onRequestDeleteSingle,
                    )
                }
            }

            if (!uiState.isSelectionMode) {
                item {
                    OutlinedButton(
                        onClick = onSettings,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp),
                        shape = MaterialTheme.shapes.large,
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.settings_title),
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Previews — test responsive (tailles/ratios) sans émulateur.
// ---------------------------------------------------------------------------

private fun sampleInterventions(): List<Intervention> = listOf(
    Intervention(
        id = 1,
        startedAtEpochMillis = 1_716_200_000_000,
        status = InterventionStatus.DRAFT,
        schemaVersion = "v2",
        nom = "DURAND",
        prenom = "Camille",
        age = 34,
    ),
    Intervention(
        id = 2,
        startedAtEpochMillis = 1_716_100_000_000,
        status = InterventionStatus.CLOSED,
        schemaVersion = "v2",
        nom = "MARTIN",
        prenom = "Jean-Baptiste",
        age = 72,
    ),
    Intervention(
        id = 3,
        startedAtEpochMillis = 1_716_000_000_000,
        status = InterventionStatus.DRAFT,
        schemaVersion = "v2",
    ),
)

@ResponsivePreviews
@Composable
private fun HomeScreenResponsivePreview() {
    NotesDuSecouristeTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            interventions = sampleInterventions(),
            isDark = false,
            onNewIntervention = {},
            onAideMemoire = {},
            onToggleTheme = {},
            onSettings = {},
            onOpenIntervention = {},
            onOpenRecap = {},
            onInterventionLongPress = {},
            onToggleSelection = {},
            onRequestDeleteSingle = {},
            onExitSelectionMode = {},
            onRequestDeleteSelected = {},
            onConfirmDelete = {},
            onDismissDeleteConfirm = {},
            onErrorConsumed = {},
        )
    }
}

@ThemePreviews
@Composable
private fun HomeScreenThemePreview() {
    NotesDuSecouristeTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            interventions = sampleInterventions(),
            isDark = false,
            onNewIntervention = {},
            onAideMemoire = {},
            onToggleTheme = {},
            onSettings = {},
            onOpenIntervention = {},
            onOpenRecap = {},
            onInterventionLongPress = {},
            onToggleSelection = {},
            onRequestDeleteSingle = {},
            onExitSelectionMode = {},
            onRequestDeleteSelected = {},
            onConfirmDelete = {},
            onDismissDeleteConfirm = {},
            onErrorConsumed = {},
        )
    }
}

@ResponsivePreviews
@Composable
private fun HomeScreenEmptyPreview() {
    NotesDuSecouristeTheme {
        HomeScreenContent(
            uiState = HomeUiState(),
            interventions = emptyList(),
            isDark = false,
            onNewIntervention = {},
            onAideMemoire = {},
            onToggleTheme = {},
            onSettings = {},
            onOpenIntervention = {},
            onOpenRecap = {},
            onInterventionLongPress = {},
            onToggleSelection = {},
            onRequestDeleteSingle = {},
            onExitSelectionMode = {},
            onRequestDeleteSelected = {},
            onConfirmDelete = {},
            onDismissDeleteConfirm = {},
            onErrorConsumed = {},
        )
    }
}
