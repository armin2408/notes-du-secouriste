package com.notesdusecouriste.feature.interventionnotes.ui.recap

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorGroup
import androidx.compose.ui.graphics.vector.VectorNode
import androidx.compose.ui.graphics.vector.VectorPath
import androidx.compose.ui.graphics.vector.toPath
import com.notesdusecouriste.feature.interventionnotes.R
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind
import com.notesdusecouriste.feature.interventionnotes.ui.components.icon

/**
 * Rasterise les **mêmes** [ImageVector] Material que l’écran note (`NoteSectionKind.icon()`).
 */
internal object PdfMaterialIcons {

    fun forMesureSectionTitle(title: String, titles: MesureSectionTitles): NoteSectionKind? =
        when (title) {
            titles.respiration -> NoteSectionKind.Respiration
            titles.circulation -> NoteSectionKind.Circulation
            titles.conscience -> NoteSectionKind.Conscience
            titles.suspicionAvc -> NoteSectionKind.SuspicionAvc
            titles.glasgow -> NoteSectionKind.Conscience
            else -> null
        }

    data class MesureSectionTitles(
        val respiration: String,
        val circulation: String,
        val conscience: String,
        val suspicionAvc: String,
        val glasgow: String,
    ) {
        companion object {
            fun from(context: android.content.Context) = MesureSectionTitles(
                respiration = context.getString(R.string.subblock_respiration),
                circulation = context.getString(R.string.subblock_circulation),
                conscience = context.getString(R.string.subblock_conscience),
                suspicionAvc = context.getString(R.string.subblock_suspicion_avc),
                glasgow = context.getString(R.string.subblock_glasgow),
            )
        }
    }

    fun toBitmap(kind: NoteSectionKind, sizePx: Int, tintArgb: Int): Bitmap =
        toBitmap(kind.icon(), sizePx, tintArgb)

    fun toBitmap(vector: ImageVector, sizePx: Int, tintArgb: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val scaleX = sizePx / vector.viewportWidth
        val scaleY = sizePx / vector.viewportHeight
        canvas.save()
        canvas.scale(scaleX, scaleY)
        renderNode(canvas, vector.root, tintArgb)
        canvas.restore()
        return bitmap
    }

    private fun renderNode(canvas: Canvas, node: VectorNode, tintArgb: Int) {
        when (node) {
            is VectorGroup -> vectorGroupChildren(node).forEach { renderNode(canvas, it, tintArgb) }
            is VectorPath -> {
                val androidPath = node.pathData.toPath().asAndroidPath()
                val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = tintArgb
                    style = Paint.Style.FILL
                }
                canvas.drawPath(androidPath, paint)
            }
        }
    }

    /**
     * `VectorGroup.children` est private en Compose — accès réfléchi uniquement pour
     * réutiliser les chemins Material officiels (FavoriteBorder, Psychology, etc.).
     */
    @Suppress("UNCHECKED_CAST")
    private fun vectorGroupChildren(group: VectorGroup): List<VectorNode> {
        val field = VectorGroup::class.java.getDeclaredField("children").apply {
            isAccessible = true
        }
        return field.get(group) as List<VectorNode>
    }
}
