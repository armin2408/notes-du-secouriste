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
import com.notesdusecouriste.core.data.model.formatNoteHeader
import com.notesdusecouriste.core.data.model.withNormalizedMesures
import com.notesdusecouriste.core.data.preferences.SecouristeProfileRepository
import com.notesdusecouriste.core.data.repository.InterventionPhotoRepository
import com.notesdusecouriste.core.data.repository.InterventionRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

data class GeneratedPdf(val file: File, val landscape: Boolean)

/**
 * Génération, enregistrement dans Téléchargements et partage des PDF de synthèse.
 */
@Singleton
class InterventionPdfExportService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: InterventionRepository,
    private val photoRepository: InterventionPhotoRepository,
    private val secouristeProfileRepository: SecouristeProfileRepository,
) {

    /**
     * @param landscape orientation imposée ; `null` = paysage seulement si le portrait tronque.
     * @return `null` si l'intervention n'a ni contenu ni photo.
     */
    suspend fun generate(
        interventionId: Long,
        landscape: Boolean? = null,
        fileNameSuffix: String = "",
    ): GeneratedPdf? {
        val content = repository.observeNoteContent(interventionId).first()
            .withNormalizedMesures()
        val recap = InterventionRecapBuilder.build(context, content)
        val photos = photoRepository.observePhotos(interventionId).first()
        if (recap.isEmpty && photos.isEmpty()) return null

        val profile = secouristeProfileRepository.profile.first()
        val photoFiles = photos.map { File(photoRepository.resolveFilePath(it.fileName)) }
        val createdAtEpochMillis = repository.getIntervention(interventionId)
            ?.startedAtEpochMillis
            ?: System.currentTimeMillis()
        return withContext(Dispatchers.IO) {
            val resolvedLandscape =
                landscape ?: InterventionRecapPdfExporter.wouldTruncateInPortrait(recap)
            val file = InterventionRecapPdfExporter.export(
                context = context,
                headerTitle = formatNoteHeader(content.victime),
                recap = recap,
                profile = profile,
                photoFiles = photoFiles,
                landscape = resolvedLandscape,
                createdAtEpochMillis = createdAtEpochMillis,
                fileNameSuffix = fileNameSuffix,
            )
            GeneratedPdf(file, resolvedLandscape)
        }
    }

    /**
     * Génère un PDF par intervention ; les interventions vides sont ignorées.
     */
    suspend fun generateAll(interventionIds: Collection<Long>): List<File> =
        interventionIds.mapNotNull { id ->
            generate(id, fileNameSuffix = "_n$id")?.file
        }

    suspend fun saveToDownloads(file: File) {
        withContext(Dispatchers.IO) { savePdfToDownloads(context, file) }
    }

    fun contentUri(file: File): Uri =
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)

    companion object {
        fun sharePdfIntent(uris: List<Uri>): Intent =
            if (uris.size == 1) {
                Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uris.first())
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            } else {
                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "application/pdf"
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
            }

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
