package com.notesdusecouriste.core.data.photos

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max

/**
 * Stockage local des photos d’intervention.
 * Ré-encodage JPEG sRGB = strip EXIF + format stable pour PDF (évite stries galerie).
 */
@Singleton
class InterventionPhotoStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    fun photosDir(): File {
        val dir = File(context.filesDir, DIR_NAME)
        if (!dir.exists()) dir.mkdirs()
        return dir
    }

    fun resolveFile(fileName: String): File = File(photosDir(), fileName)

    /**
     * Importe une image depuis un URI (galerie ou capture FileProvider),
     * ré-encode en JPEG sans EXIF, retourne le nom de fichier local.
     */
    fun importFromUri(uri: Uri): String {
        val fileName = "${UUID.randomUUID()}.jpg"
        val dest = resolveFile(fileName)
        context.contentResolver.openInputStream(uri).use { input ->
            requireNotNull(input) { "Impossible de lire l’image." }
            val options = BitmapFactory.Options().apply {
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val original = BitmapFactory.decodeStream(input, null, options)
                ?: error("Image illisible.")
            val scaled = scaleDown(original, MAX_EDGE_PX)
            if (scaled !== original) original.recycle()
            val safe = toJpegSafeBitmap(scaled)
            if (safe !== scaled) scaled.recycle()
            FileOutputStream(dest).use { out ->
                if (!safe.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)) {
                    error("Échec compression JPEG.")
                }
            }
            safe.recycle()
        }
        return fileName
    }

    /**
     * Pivote la photo de 90° (sens horaire) dans un nouveau fichier et supprime l’ancien.
     * Le nom change pour invalider les bitmaps mis en cache côté UI.
     */
    fun rotateClockwise(fileName: String): String {
        val source = resolveFile(fileName)
        val original = BitmapFactory.decodeFile(source.absolutePath)
            ?: error("Image illisible.")
        val matrix = Matrix().apply { postRotate(90f) }
        val rotated = Bitmap.createBitmap(
            original, 0, 0, original.width, original.height, matrix, true,
        )
        if (rotated !== original) original.recycle()
        val newFileName = "${UUID.randomUUID()}.jpg"
        val dest = resolveFile(newFileName)
        try {
            FileOutputStream(dest).use { out ->
                if (!rotated.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, out)) {
                    error("Échec compression JPEG.")
                }
            }
        } catch (e: Exception) {
            dest.delete()
            throw e
        } finally {
            rotated.recycle()
        }
        return newFileName
    }

    /** Copie la photo dans la galerie publique (Pictures/Notes du Secouriste). */
    fun exportToGallery(fileName: String) {
        val source = resolveFile(fileName)
        require(source.exists()) { "Photo introuvable." }
        val displayName = "NotesSecouriste_${System.currentTimeMillis()}.jpg"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = context.contentResolver
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/$GALLERY_ALBUM",
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            val uri = resolver.insert(collection, values) ?: error("Galerie indisponible.")
            try {
                resolver.openOutputStream(uri).use { out ->
                    requireNotNull(out) { "Galerie indisponible." }
                    source.inputStream().use { it.copyTo(out) }
                }
                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                resolver.update(uri, values, null, null)
            } catch (e: Exception) {
                resolver.delete(uri, null, null)
                throw e
            }
        } else {
            @Suppress("DEPRECATION")
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                GALLERY_ALBUM,
            )
            if (!dir.exists() && !dir.mkdirs()) error("Galerie indisponible.")
            val dest = File(dir, displayName)
            source.copyTo(dest, overwrite = true)
            MediaScannerConnection.scanFile(
                context,
                arrayOf(dest.absolutePath),
                arrayOf("image/jpeg"),
                null,
            )
        }
    }

    fun deleteFile(fileName: String) {
        resolveFile(fileName).delete()
    }

    fun deleteFiles(fileNames: Iterable<String>) {
        fileNames.forEach(::deleteFile)
    }

    /** Fichier temporaire pour TakePicture (cache). */
    fun createCaptureTempFile(): File {
        val dir = File(context.cacheDir, "photo_captures")
        if (!dir.exists()) dir.mkdirs()
        return File(dir, "capture_${UUID.randomUUID()}.jpg")
    }

    private fun scaleDown(bitmap: Bitmap, maxEdge: Int): Bitmap {
        val longest = max(bitmap.width, bitmap.height)
        if (longest <= maxEdge) return bitmap
        val scale = maxEdge.toFloat() / longest
        val w = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val h = (bitmap.height * scale).toInt().coerceAtLeast(1)
        return Bitmap.createScaledBitmap(bitmap, w, h, true)
    }

    companion object {
        const val DIR_NAME = "intervention_photos"
        private const val GALLERY_ALBUM = "Notes du Secouriste"
        private const val MAX_EDGE_PX = 1920
        private const val JPEG_QUALITY = 85

        /**
         * Aplatit sur fond blanc en ARGB_8888 logiciel.
         * Les photos galerie (Display P3 / configs atypiques) provoquent sinon
         * des stries colorées dans [android.graphics.pdf.PdfDocument].
         */
        fun toJpegSafeBitmap(source: Bitmap): Bitmap {
            val w = source.width.coerceAtLeast(1)
            val h = source.height.coerceAtLeast(1)
            val out = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(out)
            canvas.drawColor(Color.WHITE)
            canvas.drawBitmap(source, 0f, 0f, Paint(Paint.FILTER_BITMAP_FLAG))
            return out
        }

        /** Bitmap prêt pour PdfDocument (re-encode JPEG → décode RGB stable). */
        fun decodeForPdf(file: File, maxWidth: Int, maxHeight: Int): Bitmap? {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

            var sample = 1
            while (
                bounds.outWidth / sample > maxWidth * 2 ||
                bounds.outHeight / sample > maxHeight * 2
            ) {
                sample *= 2
            }
            val opts = BitmapFactory.Options().apply {
                inSampleSize = sample
                inPreferredConfig = Bitmap.Config.ARGB_8888
            }
            val decoded = BitmapFactory.decodeFile(file.absolutePath, opts) ?: return null
            val safe = toJpegSafeBitmap(decoded)
            if (safe !== decoded) decoded.recycle()

            val widthScale = maxWidth.toFloat() / safe.width
            val heightScale = maxHeight.toFloat() / safe.height
            val scale = minOf(1f, widthScale, heightScale)
            val targetW = ((safe.width * scale).toInt().coerceAtLeast(1) / 2) * 2
            val targetH = ((safe.height * scale).toInt().coerceAtLeast(1) / 2) * 2
            val scaled = if (targetW == safe.width && targetH == safe.height) {
                safe
            } else {
                Bitmap.createScaledBitmap(safe, targetW, targetH, true).also {
                    if (it !== safe) safe.recycle()
                }
            }

            // Round-trip JPEG : force un espace couleur que PdfDocument gère correctement.
            val bytes = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
            scaled.recycle()
            return BitmapFactory.decodeByteArray(bytes.toByteArray(), 0, bytes.size())
        }
    }
}
