package com.notesdusecouriste.feature.interventionnotes.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.notesdusecouriste.core.data.model.InterventionPhoto
import com.notesdusecouriste.core.ui.legal.DisclaimerResources
import com.notesdusecouriste.feature.interventionnotes.R
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionCard
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteStickyHeaderRegistry
import com.notesdusecouriste.feature.interventionnotes.ui.components.noteSectionHeader
import com.notesdusecouriste.feature.interventionnotes.ui.components.noteSectionItem
import java.io.File

internal fun LazyListScope.photosSection(
    viewModel: InterventionNotesViewModel,
    uiState: InterventionNotesUiState,
    context: Context,
    registry: NoteStickyHeaderRegistry,
) {
    noteSectionHeader(
        key = "header_photos",
        title = context.getString(R.string.block_photos),
        kind = NoteSectionKind.Photos,
        registry = registry,
    )
    noteSectionItem(key = "photos_body", registry = registry) {
        PhotosSectionBody(viewModel = viewModel, uiState = uiState)
    }
}

@Composable
private fun PhotosSectionBody(
    viewModel: InterventionNotesViewModel,
    uiState: InterventionNotesUiState,
) {
    val context = LocalContext.current
    var captureFile by remember { mutableStateOf<File?>(null) }
    var pendingCameraAfterPermission by remember { mutableStateOf(false) }
    var previewStartIndex by remember { mutableStateOf<Int?>(null) }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) viewModel.importPhotoFromUri(uri)
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture(),
    ) { success ->
        val file = captureFile
        captureFile = null
        if (success && file != null && file.exists()) {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
            viewModel.importPhotoFromUri(uri)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) {
            launchCamera(context, takePictureLauncher) { captureFile = it }
        } else {
            pendingCameraAfterPermission = false
            viewModel.notifyUserMessage(
                context.getString(R.string.photos_permission_denied),
            )
        }
    }

    fun startCamera() {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            launchCamera(context, takePictureLauncher) { captureFile = it }
        } else {
            pendingCameraAfterPermission = true
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(pendingCameraAfterPermission) {
        if (!pendingCameraAfterPermission) return@LaunchedEffect
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            pendingCameraAfterPermission = false
            launchCamera(context, takePictureLauncher) { captureFile = it }
        }
    }

    if (uiState.showPhotoConsentDialog) {
        AlertDialog(
            onDismissRequest = { /* obligatoire : lire puis J’ai compris */ },
            title = { Text(stringResource(DisclaimerResources.photoTitle)) },
            text = { Text(stringResource(DisclaimerResources.photoBody)) },
            confirmButton = {
                TextButton(onClick = viewModel::acknowledgePhotosConsent) {
                    Text(stringResource(DisclaimerResources.photoAckButton))
                }
            },
        )
    }

    if (uiState.showPhotoSourceDialog) {
        AlertDialog(
            onDismissRequest = viewModel::dismissPhotoSourceDialog,
            title = { Text(stringResource(R.string.photos_source_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            viewModel.dismissPhotoSourceDialog()
                            startCamera()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Outlined.PhotoCamera, contentDescription = null)
                        Text(
                            text = stringResource(R.string.photos_take),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                    TextButton(
                        onClick = {
                            viewModel.dismissPhotoSourceDialog()
                            galleryLauncher.launch("image/*")
                        },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Icon(Icons.Outlined.PhotoLibrary, contentDescription = null)
                        Text(
                            text = stringResource(R.string.photos_gallery),
                            modifier = Modifier.padding(start = 8.dp),
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = viewModel::dismissPhotoSourceDialog) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    uiState.photoPendingDeleteId?.let {
        AlertDialog(
            onDismissRequest = viewModel::dismissDeletePhoto,
            title = { Text(stringResource(R.string.photos_delete_title)) },
            text = { Text(stringResource(R.string.photos_delete_message)) },
            confirmButton = {
                TextButton(onClick = viewModel::confirmDeletePhoto) {
                    Text(
                        text = stringResource(R.string.photos_delete_confirm),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = viewModel::dismissDeletePhoto) {
                    Text(stringResource(R.string.action_cancel))
                }
            },
        )
    }

    previewStartIndex?.let { startIndex ->
        if (uiState.photos.isNotEmpty()) {
            PhotoFullscreenViewer(
                photos = uiState.photos,
                resolvePath = viewModel::resolvePhotoPath,
                initialIndex = startIndex.coerceIn(0, uiState.photos.lastIndex),
                onDismiss = { previewStartIndex = null },
            )
        } else {
            previewStartIndex = null
        }
    }

    NoteSectionCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (uiState.photos.isEmpty()) {
                Text(
                    text = stringResource(R.string.photos_empty_hint),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp),
                ) {
                    items(
                        count = uiState.photos.size,
                        key = { index -> uiState.photos[index].id },
                    ) { index ->
                        val photo = uiState.photos[index]
                        PhotoThumbnail(
                            path = viewModel.resolvePhotoPath(photo.fileName),
                            onOpen = { previewStartIndex = index },
                            onDelete = { viewModel.requestDeletePhoto(photo.id) },
                        )
                    }
                }
            }

            if (uiState.isImportingPhoto) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(28.dp))
                }
            }

            OutlinedButton(
                onClick = viewModel::onAddPhotoClicked,
                enabled = !uiState.isImportingPhoto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Icon(Icons.Outlined.AddAPhoto, contentDescription = null)
                Text(
                    text = stringResource(R.string.photos_add),
                    modifier = Modifier.padding(start = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun PhotoThumbnail(
    path: String,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    val bitmap = remember(path) {
        BitmapFactory.decodeFile(path)?.asImageBitmap()
    }
    Box(
        modifier = Modifier
            .width(120.dp)
            .height(120.dp)
            .clip(RoundedCornerShape(8.dp)),
    ) {
        val imageModifier = Modifier
            .matchParentSize()
            .clickable(onClick = onOpen)
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = stringResource(R.string.photos_content_description),
                contentScale = ContentScale.Crop,
                modifier = imageModifier,
            )
        } else {
            Box(
                modifier = imageModifier.background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Text("?", style = MaterialTheme.typography.titleLarge)
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(6.dp)
                .size(26.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.88f),
                    shape = CircleShape,
                )
                .clickable(onClick = onDelete),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Outlined.Delete,
                contentDescription = stringResource(R.string.photos_delete_confirm),
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(15.dp),
            )
        }
    }
}

@Composable
private fun PhotoFullscreenViewer(
    photos: List<InterventionPhoto>,
    resolvePath: (String) -> String,
    initialIndex: Int,
    onDismiss: () -> Unit,
) {
    val pagerState = rememberPagerState(
        initialPage = initialIndex,
        pageCount = { photos.size },
    )
    var currentPageScale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(pagerState.currentPage) {
        currentPageScale = 1f
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
        ) {
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = currentPageScale <= 1.01f,
                modifier = Modifier.fillMaxSize(),
            ) { page ->
                val photo = photos[page]
                val path = resolvePath(photo.fileName)
                val bitmap = remember(path) {
                    BitmapFactory.decodeFile(path)?.asImageBitmap()
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (bitmap != null) {
                        ZoomablePhotoImage(
                            bitmap = bitmap,
                            contentDescription = stringResource(R.string.photos_content_description),
                            onScaleChange = { scale ->
                                if (page == pagerState.currentPage) {
                                    currentPageScale = scale
                                }
                            },
                        )
                    } else {
                        Text(
                            text = "?",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge,
                        )
                    }
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .statusBarsPadding()
                    .padding(8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.45f),
                        shape = CircleShape,
                    ),
            ) {
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.photos_close_preview),
                    tint = Color.White,
                )
            }

            if (photos.size > 1) {
                Text(
                    text = stringResource(
                        R.string.photos_preview_counter,
                        pagerState.currentPage + 1,
                        photos.size,
                    ),
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .statusBarsPadding()
                        .padding(top = 16.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun ZoomablePhotoImage(
    bitmap: androidx.compose.ui.graphics.ImageBitmap,
    contentDescription: String,
    onScaleChange: (Float) -> Unit,
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    LaunchedEffect(scale) {
        onScaleChange(scale)
    }

    Image(
        bitmap = bitmap,
        contentDescription = contentDescription,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .fillMaxSize()
            // pointerInput avant graphicsLayer : deltas en pixels écran (1:1 avec le doigt).
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown(requireUnconsumed = false)
                    do {
                        val event = awaitPointerEvent()
                        val pressed = event.changes.filter { it.pressed }
                        when {
                            pressed.size >= 2 -> {
                                val zoomChange = event.calculateZoom()
                                val panChange = event.calculatePan()
                                val newScale = (scale * zoomChange).coerceIn(1f, 5f)
                                scale = newScale
                                offset = if (newScale > 1f) offset + panChange else Offset.Zero
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
            }
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                translationX = offset.x
                translationY = offset.y
            },
    )
}

private fun launchCamera(
    context: Context,
    launcher: androidx.activity.result.ActivityResultLauncher<Uri>,
    onFileReady: (File) -> Unit,
) {
    val dir = File(context.cacheDir, "photo_captures").also { if (!it.exists()) it.mkdirs() }
    val file = File(dir, "capture_${System.currentTimeMillis()}.jpg")
    onFileReady(file)
    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file,
    )
    launcher.launch(uri)
}
