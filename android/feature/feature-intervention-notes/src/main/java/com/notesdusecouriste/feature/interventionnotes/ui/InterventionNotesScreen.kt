package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.core.ui.systembars.navigationBarBottomPadding
import com.notesdusecouriste.core.ui.systembars.scaffoldContentWithoutNavigationBar
import com.notesdusecouriste.feature.interventionnotes.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterventionNotesScreen(
    interventionId: Long,
    onBack: () -> Unit,
    onRecap: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
    viewModel: InterventionNotesViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        val message = uiState.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearErrorMessage()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = scaffoldContentWithoutNavigationBar(),
        topBar = {
            TopAppBar(
                title = {
                    ColumnTitle(
                        title = uiState.headerTitle,
                        subtitle = saveStatusText(uiState),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour",
                        )
                    }
                },
                actions = actions,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        InterventionNotesListContent(
            viewModel = viewModel,
            onRecap = onRecap,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(
                bottom = navigationBarBottomPadding(extra = 16.dp),
            ),
        )
    }
}

@Composable
private fun ColumnTitle(title: String, subtitle: String?) {
    Column {
        Text(text = title, style = MaterialTheme.typography.titleLarge)
        if (!subtitle.isNullOrBlank()) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun saveStatusText(uiState: InterventionNotesUiState): String? = when {
    uiState.isSaving -> stringResource(R.string.notes_saving)
    uiState.lastSavedAtEpochMillis != null -> stringResource(R.string.notes_saved)
    else -> null
}
