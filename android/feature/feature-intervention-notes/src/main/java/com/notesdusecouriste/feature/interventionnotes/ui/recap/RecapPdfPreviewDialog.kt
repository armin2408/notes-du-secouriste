package com.notesdusecouriste.feature.interventionnotes.ui.recap

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.outlined.ScreenRotation
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.notesdusecouriste.feature.interventionnotes.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecapPdfPreviewDialog(
    pdfFile: File,
    landscape: Boolean,
    isExporting: Boolean,
    onDismiss: () -> Unit,
    onShare: () -> Unit,
    onAideMemoire: () -> Unit,
    onToggleOrientation: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
        ),
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color(0xFFE8E8E8),
            topBar = {
                TopAppBar(
                    title = {
                        ColumnTitle(
                            landscape = landscape,
                            isExporting = isExporting,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onDismiss) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.recap_back),
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = onAideMemoire) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.MenuBook,
                                contentDescription = stringResource(R.string.recap_pdf_aide_memoire),
                            )
                        }
                        IconButton(
                            onClick = onToggleOrientation,
                            enabled = !isExporting,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ScreenRotation,
                                contentDescription = stringResource(R.string.pdf_toggle_orientation),
                            )
                        }
                        IconButton(onClick = onShare, enabled = !isExporting) {
                            Icon(
                                imageVector = Icons.Outlined.Share,
                                contentDescription = stringResource(R.string.recap_share_pdf),
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    ),
                )
            },
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFE8E8E8)),
            ) {
                PdfPagesViewer(
                    pdfFile = pdfFile,
                    modifier = Modifier.fillMaxSize(),
                )
                if (isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnTitle(
    landscape: Boolean,
    isExporting: Boolean,
) {
    Column {
        Text(stringResource(R.string.recap_pdf_preview_title))
        Text(
            text = stringResource(
                if (landscape) R.string.pdf_orientation_landscape
                else R.string.pdf_orientation_portrait,
            ) + if (isExporting) "…" else "",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun PdfPagesViewer(
    pdfFile: File,
    modifier: Modifier = Modifier,
) {
    var pages by remember(pdfFile.absolutePath, pdfFile.lastModified()) {
        mutableStateOf<List<Bitmap>>(emptyList())
    }
    var loading by remember(pdfFile.absolutePath, pdfFile.lastModified()) { mutableStateOf(true) }
    val listState = rememberLazyListState()
    var scale by remember(pdfFile.absolutePath) { mutableFloatStateOf(1f) }
    var offset by remember(pdfFile.absolutePath) { mutableStateOf(Offset.Zero) }

    LaunchedEffect(pdfFile.absolutePath, pdfFile.lastModified()) {
        loading = true
        scale = 1f
        offset = Offset.Zero
        pages = withContext(Dispatchers.IO) { renderPdfPages(pdfFile) }
        loading = false
    }

    DisposableEffect(pdfFile.absolutePath) {
        onDispose {
            pages.forEach { it.recycle() }
        }
    }

    when {
        loading && pages.isEmpty() -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        pages.isEmpty() -> {
            Box(modifier = modifier, contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.recap_export_pdf_error),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        else -> {
            Box(
                modifier = modifier.pointerInput(Unit) {
                    awaitEachGesture {
                        awaitFirstDown(requireUnconsumed = false)
                        do {
                            val event = awaitPointerEvent()
                            val pressed = event.changes.filter { it.pressed }
                            when {
                                pressed.size >= 2 -> {
                                    val zoomChange = event.calculateZoom()
                                    val panChange = event.calculatePan()
                                    val centroid = event.calculateCentroid()
                                    val oldScale = scale
                                    val newScale = (oldScale * zoomChange).coerceIn(1f, 4f)
                                    if (newScale != oldScale && oldScale > 0f) {
                                        // Garde le point sous les doigts fixe pendant le zoom.
                                        offset = centroid - (centroid - offset) * (newScale / oldScale)
                                        scale = newScale
                                    }
                                    offset += panChange
                                    if (scale <= 1.01f) {
                                        scale = 1f
                                        offset = Offset.Zero
                                    }
                                    pressed.forEach { it.consume() }
                                }
                                pressed.size == 1 && scale > 1.01f -> {
                                    val change = pressed.first()
                                    val drag = change.positionChange()
                                    if (drag != Offset.Zero) {
                                        offset += drag
                                        change.consume()
                                    }
                                }
                            }
                        } while (event.changes.any { it.pressed })
                    }
                },
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                            transformOrigin = TransformOrigin(0f, 0f)
                        },
                    contentPadding = PaddingValues(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    itemsIndexed(
                        items = pages,
                        key = { index, _ -> "${pdfFile.absolutePath}_$index" },
                    ) { _, bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(
                                    bitmap.width.toFloat() /
                                        bitmap.height.toFloat().coerceAtLeast(1f),
                                )
                                .background(Color.White),
                        )
                    }
                }
            }
        }
    }
}

private fun renderPdfPages(file: File): List<Bitmap> {
    val descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
    return try {
        PdfRenderer(descriptor).use { renderer ->
            List(renderer.pageCount) { index ->
                renderer.openPage(index).use { page ->
                    val scale = 2
                    val bitmap = Bitmap.createBitmap(
                        page.width * scale,
                        page.height * scale,
                        Bitmap.Config.ARGB_8888,
                    )
                    bitmap.eraseColor(AndroidColor.WHITE)
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                    bitmap
                }
            }
        }
    } finally {
        descriptor.close()
    }
}
