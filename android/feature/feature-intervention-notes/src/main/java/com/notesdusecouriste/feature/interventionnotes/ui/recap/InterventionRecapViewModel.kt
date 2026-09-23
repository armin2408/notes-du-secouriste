package com.notesdusecouriste.feature.interventionnotes.ui.recap

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notesdusecouriste.core.data.model.formatNoteHeader
import com.notesdusecouriste.core.data.model.withNormalizedMesures
import com.notesdusecouriste.core.data.preferences.RecapPreferencesRepository
import com.notesdusecouriste.core.data.preferences.SecouristeProfile
import com.notesdusecouriste.core.data.preferences.SecouristeProfileRepository
import com.notesdusecouriste.core.data.repository.InterventionPhotoRepository
import com.notesdusecouriste.core.data.repository.InterventionRepository
import com.notesdusecouriste.feature.interventionnotes.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject

data class InterventionRecapUiState(
    val headerTitle: String = "",
    val recap: InterventionRecap = InterventionRecap(),
    val mesuresColumnsExpanded: Boolean = false,
    val isExportingPdf: Boolean = false,
    val pdfPreviewFile: File? = null,
    val photoCount: Int = 0,
    val pdfLandscape: Boolean = false,
    val contentLoaded: Boolean = false,
)

sealed interface RecapExportEvent {
    data class SharePdf(val uri: Uri) : RecapExportEvent
    data class SavedToDownloads(val message: String) : RecapExportEvent
    data class Error(val message: String) : RecapExportEvent
}

@HiltViewModel
class InterventionRecapViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val repository: InterventionRepository,
    private val photoRepository: InterventionPhotoRepository,
    private val recapPreferences: RecapPreferencesRepository,
    private val secouristeProfileRepository: SecouristeProfileRepository,
) : ViewModel() {
    private val interventionId: Long =
        checkNotNull(savedStateHandle.get<Long>("interventionId")) {
            "interventionId required"
        }

    private val recapContentState =
        combine(
            repository.observeNoteContent(interventionId),
            photoRepository.observePhotos(interventionId),
        ) { content, photos ->
            val normalized = content.withNormalizedMesures()
            InterventionRecapUiState(
                headerTitle = formatNoteHeader(normalized.victime),
                recap = InterventionRecapBuilder.build(context, normalized),
                photoCount = photos.size,
            )
        }

    private val exporting = MutableStateFlow(false)
    private val pdfPreviewFile = MutableStateFlow<File?>(null)
    private val pdfLandscape = MutableStateFlow(false)
    private val orientationManual = MutableStateFlow(false)
    private val contentLoaded = MutableStateFlow(false)

    init {
        viewModelScope.launch {
            var previousProfile: SecouristeProfile? = null
            secouristeProfileRepository.profile.collect { current ->
                val hadPreview = pdfPreviewFile.value != null
                if (previousProfile != null && previousProfile != current && hadPreview) {
                    exportPdf()
                }
                previousProfile = current
            }
        }
        // Notes / questionnaires / photos changés pendant l’aperçu → régénérer le PDF.
        viewModelScope.launch {
            var previousKey: String? = null
            recapContentState.collect { state ->
                contentLoaded.value = true
                val key = contentFingerprint(state)
                val hadPreview = pdfPreviewFile.value != null
                if (previousKey != null && previousKey != key && hadPreview) {
                    exportPdf()
                }
                previousKey = key
            }
        }
    }

    val uiState: StateFlow<InterventionRecapUiState> =
        combine(
            combine(
                recapContentState,
                recapPreferences.mesuresColumnsExpanded,
                exporting,
                pdfPreviewFile,
                pdfLandscape,
            ) { content, columnsExpanded, isExporting, preview, landscape ->
                content.copy(
                    mesuresColumnsExpanded = columnsExpanded,
                    isExportingPdf = isExporting,
                    pdfPreviewFile = preview,
                    pdfLandscape = landscape,
                )
            },
            contentLoaded,
        ) { state, loaded ->
            state.copy(contentLoaded = loaded)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InterventionRecapUiState(),
        )

    private val _exportEvents = MutableSharedFlow<RecapExportEvent>(extraBufferCapacity = 1)
    val exportEvents: SharedFlow<RecapExportEvent> = _exportEvents.asSharedFlow()

    fun toggleMesuresColumnsExpanded() {
        viewModelScope.launch {
            val expanded = !uiState.value.mesuresColumnsExpanded
            recapPreferences.setMesuresColumnsExpanded(expanded)
        }
    }

    fun togglePdfOrientation() {
        orientationManual.value = true
        pdfLandscape.value = !pdfLandscape.value
        exportPdf()
    }

    fun exportPdf() {
        if (exporting.value) return
        viewModelScope.launch {
            exporting.value = true
            try {
                val content = repository.observeNoteContent(interventionId).first()
                    .withNormalizedMesures()
                val recap = InterventionRecapBuilder.build(context, content)
                val headerTitle = formatNoteHeader(content.victime)
                val photos = photoRepository.observePhotos(interventionId).first()
                if (recap.isEmpty && photos.isEmpty()) {
                    _exportEvents.emit(
                        RecapExportEvent.Error(context.getString(R.string.recap_export_pdf_empty)),
                    )
                    return@launch
                }
                val profile = secouristeProfileRepository.profile.first()
                val photoFiles = photos.map { photo ->
                    File(photoRepository.resolveFilePath(photo.fileName))
                }
                val landscape = if (orientationManual.value) {
                    pdfLandscape.value
                } else {
                    val needsLandscape = withContext(Dispatchers.IO) {
                        InterventionRecapPdfExporter.wouldTruncateInPortrait(recap)
                    }
                    pdfLandscape.value = needsLandscape
                    needsLandscape
                }
                val createdAtEpochMillis = repository.getIntervention(interventionId)
                    ?.startedAtEpochMillis
                    ?: System.currentTimeMillis()
                val file = withContext(Dispatchers.IO) {
                    InterventionRecapPdfExporter.export(
                        context = context,
                        headerTitle = headerTitle,
                        recap = recap,
                        profile = profile,
                        photoFiles = photoFiles,
                        landscape = landscape,
                        createdAtEpochMillis = createdAtEpochMillis,
                    )
                }
                pdfPreviewFile.value = file
            } catch (e: Exception) {
                _exportEvents.emit(
                    RecapExportEvent.Error(
                        e.message?.takeIf { it.isNotBlank() }
                            ?: context.getString(R.string.recap_export_pdf_error),
                    ),
                )
            } finally {
                exporting.value = false
            }
        }
    }

    fun dismissPdfPreview() {
        pdfPreviewFile.value = null
        orientationManual.value = false
        pdfLandscape.value = false
    }

    fun sharePdfPreview() {
        val file = pdfPreviewFile.value ?: return
        viewModelScope.launch {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file,
            )
            _exportEvents.emit(RecapExportEvent.SharePdf(uri))
        }
    }

    fun downloadPdfPreview() {
        val file = pdfPreviewFile.value ?: return
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) { savePdfToDownloads(context, file) }
                _exportEvents.emit(
                    RecapExportEvent.SavedToDownloads(
                        context.getString(R.string.recap_download_pdf_success),
                    ),
                )
            } catch (_: Exception) {
                _exportEvents.emit(
                    RecapExportEvent.Error(
                        context.getString(R.string.recap_download_pdf_error),
                    ),
                )
            }
        }
    }

    companion object {
        fun sharePdfIntent(uri: Uri): Intent =
            Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

        private fun contentFingerprint(state: InterventionRecapUiState): String =
            "${state.recap}|${state.photoCount}|${state.headerTitle}"

        private fun savePdfToDownloads(context: Context, source: File) {
            val displayName = source.name.ifBlank { "recap.pdf" }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    put(MediaStore.MediaColumns.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                    ?: error("insert Downloads failed")
                resolver.openOutputStream(uri)?.use { out ->
                    FileInputStream(source).use { input -> input.copyTo(out) }
                } ?: error("openOutputStream failed")
                values.clear()
                values.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } else {
                @Suppress("DEPRECATION")
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                if (!dir.exists()) dir.mkdirs()
                val dest = File(dir, displayName)
                FileInputStream(source).use { input ->
                    FileOutputStream(dest).use { output -> input.copyTo(output) }
                }
                MediaScannerConnection.scanFile(
                    context,
                    arrayOf(dest.absolutePath),
                    arrayOf("application/pdf"),
                    null,
                )
            }
        }
    }
}
