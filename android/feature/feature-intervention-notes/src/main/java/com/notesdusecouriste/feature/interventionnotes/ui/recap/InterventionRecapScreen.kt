package com.notesdusecouriste.feature.interventionnotes.ui.recap

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.feature.interventionnotes.R
import kotlinx.coroutines.flow.collectLatest

/**
 * Écran Synthèse = aperçu PDF plein écran (plus de page Récap intermédiaire).
 */
@Composable
fun InterventionRecapScreen(
    onBack: () -> Unit,
    onAideMemoire: () -> Unit = {},
    @Suppress("UNUSED_PARAMETER") actions: @Composable RowScope.() -> Unit = {},
    viewModel: InterventionRecapViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.contentLoaded) {
        if (uiState.contentLoaded && uiState.pdfPreviewFile == null && !uiState.isExportingPdf) {
            viewModel.exportPdf()
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.exportEvents.collectLatest { event ->
            when (event) {
                is RecapExportEvent.SharePdf -> {
                    context.startActivity(
                        Intent.createChooser(
                            InterventionRecapViewModel.sharePdfIntent(event.uri),
                            context.getString(R.string.recap_share_pdf),
                        ),
                    )
                }
                is RecapExportEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                    onBack()
                }
            }
        }
    }

    val previewFile = uiState.pdfPreviewFile
    if (previewFile != null) {
        RecapPdfPreviewDialog(
            pdfFile = previewFile,
            landscape = uiState.pdfLandscape,
            isExporting = uiState.isExportingPdf,
            onDismiss = {
                viewModel.dismissPdfPreview()
                onBack()
            },
            onShare = viewModel::sharePdfPreview,
            onAideMemoire = onAideMemoire,
            onToggleOrientation = viewModel::togglePdfOrientation,
        )
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
        ) { _ ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
