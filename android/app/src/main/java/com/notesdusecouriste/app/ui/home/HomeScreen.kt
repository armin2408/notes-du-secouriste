package com.notesdusecouriste.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.app.R
import com.notesdusecouriste.app.ui.components.AideMemoireThemeActions
import com.notesdusecouriste.app.ui.theme.AppThemeViewModel
import com.notesdusecouriste.app.ui.theme.appThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
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
    val snackbarHostState = remember { SnackbarHostState() }
    val topBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
    )

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearError()
        }
    }

    uiState.deleteConfirm?.let { request ->
        val message = when (request) {
            is DeleteConfirmRequest.Single -> stringResource(R.string.delete_confirm_single)
            is DeleteConfirmRequest.Multiple ->
                stringResource(R.string.delete_confirm_multiple, request.ids.size)
        }
        AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteConfirm() },
            title = { Text(stringResource(R.string.delete_confirm_title)) },
            text = { Text(message) },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.confirmDelete() },
                    enabled = !uiState.isDeleting,
                ) {
                    Text(stringResource(R.string.delete_confirm_action))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.dismissDeleteConfirm() },
                    enabled = !uiState.isDeleting,
                ) {
                    Text(stringResource(R.string.delete_cancel))
                }
            },
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
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
                        IconButton(onClick = { viewModel.exitSelectionMode() }) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = stringResource(R.string.home_selection_cancel),
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { viewModel.requestDeleteSelected() },
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.headlineSmall,
                            )
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Text(
                                    text = stringResource(R.string.app_beta_badge),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                    },
                    actions = {
                        AideMemoireThemeActions(
                            onAideMemoire = onAideMemoire,
                            isDark = isDark,
                            onToggleTheme = { themeViewModel.toggleTheme(isDark) },
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
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (!uiState.isSelectionMode) {
                item {
                    Button(
                        onClick = { viewModel.createIntervention(onNewIntervention) },
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
                        onLongPress = viewModel::onInterventionLongPress,
                        onToggleSelection = viewModel::toggleSelection,
                        onDeleteClick = viewModel::requestDeleteSingle,
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
