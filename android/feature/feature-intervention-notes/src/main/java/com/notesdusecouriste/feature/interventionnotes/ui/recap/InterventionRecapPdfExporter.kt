package com.notesdusecouriste.feature.interventionnotes.ui.recap

import android.content.Context
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.text.TextPaint
import com.notesdusecouriste.core.data.photos.InterventionPhotoStore
import com.notesdusecouriste.core.data.preferences.SecouristeProfile
import com.notesdusecouriste.core.ui.legal.DisclaimerResources
import com.notesdusecouriste.feature.interventionnotes.R
import com.notesdusecouriste.feature.interventionnotes.ui.components.NoteSectionKind
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/**
 * Export PDF : cartes colonnes, tableau mesures condensé, pied de page, portrait/paysage.
 */
object InterventionRecapPdfExporter {

    private const val PortraitWidth = 595f
    private const val PortraitHeight = 842f
    private const val Margin = 32f
    private const val FooterHeight = 22f
    private const val CardPad = 10f
    private const val Radius = 8f
    private const val MaxPhotoHeight = 260f
    private const val IconDrawSize = 14f
    private const val IconRasterSize = 96
    private const val IconDrawSizeSmall = 11f
    private const val IconRasterSizeSmall = 72

    private val ColorInk = 0xFF18212B.toInt()
    private val ColorMuted = 0xFF5B6875.toInt()
    private val ColorAccent = 0xFF1565C0.toInt()
    private val ColorCardBg = 0xFFF3F6F9.toInt()
    private val ColorCardWhite = 0xFFFFFFFF.toInt()
    private val ColorCardStroke = 0xFFDDE4EA.toInt()
    private val ColorSecouristeStroke = 0xFF90CAF9.toInt()
    private val ColorPageBg = 0xFFFFFFFF.toInt()
    private val ColorHeaderBg = 0xFFEEF3F8.toInt()
    private val ColorGrid = 0xFFCBD4DD.toInt()
    private val ColorGreenBg = 0xFFDDF3E2.toInt()
    private val ColorGreenFg = 0xFF1E6B35.toInt()
    private val ColorYellowBg = 0xFFFFF1C2.toInt()
    private val ColorYellowFg = 0xFF765A00.toInt()
    private val ColorOrangeBg = 0xFFFFE0C2.toInt()
    private val ColorOrangeFg = 0xFFA94D00.toInt()

    fun export(
        context: Context,
        headerTitle: String,
        recap: InterventionRecap,
        profile: SecouristeProfile,
        photoFiles: List<File> = emptyList(),
        landscape: Boolean = false,
    ): File {
        val stamp = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.FRANCE).format(Date())
        val fileStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.FRANCE).format(Date())
        val totalPages = Renderer(
            context = context,
            recap = recap,
            profile = profile,
            photoFiles = photoFiles,
            landscape = landscape,
            stamp = stamp,
            dryRun = true,
            totalPagesHint = 1,
        ).run()

        val document = PdfDocument()
        Renderer(
            context = context,
            recap = recap,
            profile = profile,
            photoFiles = photoFiles,
            landscape = landscape,
            stamp = stamp,
            dryRun = false,
            totalPagesHint = totalPages,
            document = document,
        ).run()

        val outDir = File(context.cacheDir, "exports").apply { mkdirs() }
        val safeName = headerTitle
            .ifBlank { "intervention" }
            .replace(Regex("[^A-Za-z0-9_-]+"), "_")
            .trim('_')
            .take(40)
            .ifBlank { "intervention" }
        val orientationTag = if (landscape) "paysage" else "portrait"
        val outFile = File(outDir, "recap_${safeName}_${orientationTag}_$fileStamp.pdf")
        FileOutputStream(outFile).use { stream ->
            document.writeTo(stream)
            stream.flush()
        }
        document.close()
        check(outFile.exists() && outFile.length() > 0L) {
            "PDF non écrit : ${outFile.absolutePath}"
        }
        return outFile
    }

    /**
     * True si, en portrait, au moins une cellule du tableau MESURES serait tronquée (« … »).
     */
    fun wouldTruncateInPortrait(recap: InterventionRecap): Boolean {
        val table = recap.measuresTable ?: return false
        val contentWidth = PortraitWidth - Margin * 2
        val cols = table.columnHeaders.size.coerceAtLeast(1)
        val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 7.5f
        }
        val cellPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            textSize = 7.5f
            textAlign = Paint.Align.CENTER
        }
        val measureLabels = table.rows.mapNotNull { (it as? RecapTableRow.Measure)?.label }
        val labelColW = measureLabels.maxOfOrNull { labelPaint.measureText(it) + 10f }
            ?.coerceIn(56f, contentWidth * 0.32f)
            ?: 90f
        val colW = (contentWidth - labelColW) / cols
        table.rows.forEach { row ->
            if (row is RecapTableRow.Measure) {
                if (needsEllipsis(row.label, labelPaint, labelColW - 6f)) return true
                row.cells.forEach { cell ->
                    val text = cell.text.ifBlank { "—" }
                    if (needsEllipsis(text, cellPaint, colW - 3f)) return true
                }
            }
        }
        return false
    }

    private fun needsEllipsis(text: String, paint: TextPaint, maxWidth: Float): Boolean =
        paint.measureText(text) > maxWidth

    private class Renderer(
        private val context: Context,
        private val recap: InterventionRecap,
        private val profile: SecouristeProfile,
        private val photoFiles: List<File>,
        private val landscape: Boolean,
        private val stamp: String,
        private val dryRun: Boolean,
        private val totalPagesHint: Int,
        private val document: PdfDocument? = null,
    ) {
        private val pageWidth = if (landscape) PortraitHeight else PortraitWidth
        private val pageHeight = if (landscape) PortraitWidth else PortraitHeight
        private val contentWidth = pageWidth - Margin * 2
        private val contentBottom = pageHeight - Margin - FooterHeight
        private val mesureTitles = PdfMaterialIcons.MesureSectionTitles.from(context)

        private val titlePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 18f
            color = ColorInk
        }
        private val sectionPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 11f
            color = ColorAccent
        }
        private val sectionPaintNormal = TextPaint(sectionPaint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        }
        private val labelPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT
            textSize = 7.5f
            color = ColorMuted
        }
        private val valuePaintBold = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textSize = 10f
            color = ColorInk
        }
        private val valuePaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT
            textSize = 10f
            color = ColorInk
        }
        private val bodyPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT
            textSize = 9f
            color = ColorInk
        }
        private val mutedPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT
            textSize = 8f
            color = ColorMuted
        }
        private val cellPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT
            textSize = 7.5f
            color = ColorInk
            textAlign = Paint.Align.CENTER
        }
        private val cellHeaderPaint = TextPaint(cellPaint).apply {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            color = ColorMuted
        }
        private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
            color = ColorCardStroke
        }
        private val iconPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        private val footerPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = Typeface.DEFAULT
            textSize = 8f
            color = ColorMuted
        }

        private var pageNumber = 1
        private var page: PdfDocument.Page? = null
        private var canvas: android.graphics.Canvas? = null
        private var y = Margin

        fun run(): Int {
            startNewPage()
            drawContent()
            finishCurrentPage()
            return pageNumber
        }

        private fun startNewPage() {
            if (!dryRun) {
                val doc = document!!
                val info = PdfDocument.PageInfo.Builder(
                    pageWidth.toInt(),
                    pageHeight.toInt(),
                    pageNumber,
                ).create()
                page = doc.startPage(info)
                canvas = page!!.canvas
                canvas!!.drawColor(ColorPageBg)
            }
            y = Margin
        }

        private fun finishCurrentPage() {
            if (!dryRun) {
                drawFooter(pageNumber, totalPagesHint)
                document!!.finishPage(page!!)
                page = null
                canvas = null
            }
        }

        private fun ensureSpace(needed: Float) {
            if (y + needed <= contentBottom) return
            finishCurrentPage()
            pageNumber += 1
            startNewPage()
        }

        private fun drawFooter(pageIndex: Int, total: Int) {
            val c = canvas ?: return
            val footerY = pageHeight - Margin + 4f
            c.drawText(
                context.getString(R.string.pdf_generated_at, stamp),
                Margin,
                footerY,
                footerPaint,
            )
            val pageText = context.getString(R.string.pdf_page_of, pageIndex, total)
            footerPaint.textAlign = Paint.Align.RIGHT
            c.drawText(pageText, pageWidth - Margin, footerY, footerPaint)
            footerPaint.textAlign = Paint.Align.LEFT
        }

        private fun drawWrapped(
            text: String,
            paint: TextPaint,
            x: Float = Margin,
            maxWidth: Float = contentWidth,
            spacingAfter: Float = 4f,
        ) {
            val lines = wrapText(text, paint, maxWidth)
            val lineHeight = paint.fontSpacing
            ensureSpace(lines.size * lineHeight + spacingAfter)
            if (!dryRun) {
                lines.forEach { line ->
                    canvas!!.drawText(line, x, y + paint.textSize, paint)
                    y += lineHeight
                }
            } else {
                y += lines.size * lineHeight
            }
            y += spacingAfter
        }

        private fun drawIcon(kind: NoteSectionKind, x: Float, top: Float, drawSize: Float, rasterSize: Int) {
            if (dryRun) return
            val bmp = PdfMaterialIcons.toBitmap(kind, rasterSize, ColorAccent)
            val dst = RectF(x, top, x + drawSize, top + drawSize)
            canvas!!.drawBitmap(bmp, null, dst, iconPaint)
            bmp.recycle()
        }

        private fun drawSectionTitle(title: String, kind: NoteSectionKind, titleBold: Boolean = true) {
            ensureSpace(26f)
            y += 8f
            drawIcon(kind, Margin, y + 1f, IconDrawSize, IconRasterSize)
            val paint = if (titleBold) sectionPaint else sectionPaintNormal
            if (!dryRun) {
                canvas!!.drawText(title, Margin + IconDrawSize + 6f, y + paint.textSize, paint)
            }
            y += paint.fontSpacing + 6f
        }

        /** Une ligne, N colonnes (poids optionnels). Valeurs non grasses pour SECOURISTE. */
        private fun drawColumnsCard(
            lines: List<RecapLine>,
            emptyMessage: String? = null,
            backgroundColor: Int = ColorCardBg,
            strokeColor: Int = ColorCardStroke,
            strokeWidth: Float = 1f,
            columnWeights: List<Float>? = null,
            boldValues: Boolean = true,
            columnsPerRow: Int = 4,
        ) {
            val valueP = if (boldValues) valuePaintBold else valuePaint
            if (lines.isEmpty()) {
                val height = CardPad * 2 + 18f
                ensureSpace(height + 6f)
                if (!dryRun) {
                    val rect = RectF(Margin, y, Margin + contentWidth, y + height)
                    fillPaint.color = backgroundColor
                    canvas!!.drawRoundRect(rect, Radius, Radius, fillPaint)
                    strokePaint.color = strokeColor
                    strokePaint.strokeWidth = strokeWidth
                    canvas!!.drawRoundRect(rect, Radius, Radius, strokePaint)
                    strokePaint.strokeWidth = 1f
                    emptyMessage?.let {
                        canvas!!.drawText(it, Margin + CardPad, y + CardPad + mutedPaint.textSize, mutedPaint)
                    }
                }
                y += height + 6f
                return
            }

            val rows = lines.chunked(columnsPerRow)
            val rowH = 26f
            val height = CardPad * 2 + rows.size * rowH
            ensureSpace(height + 6f)
            if (!dryRun) {
                val rect = RectF(Margin, y, Margin + contentWidth, y + height)
                fillPaint.color = backgroundColor
                canvas!!.drawRoundRect(rect, Radius, Radius, fillPaint)
                strokePaint.color = strokeColor
                strokePaint.strokeWidth = strokeWidth
                canvas!!.drawRoundRect(rect, Radius, Radius, strokePaint)
                strokePaint.strokeWidth = 1f

                var rowY = y + CardPad
                rows.forEach { rowLines ->
                    val weights = (columnWeights ?: List(rowLines.size) { 1f }).take(rowLines.size)
                    val weightSum = weights.sum().coerceAtLeast(0.01f)
                    val gap = 8f
                    val usable = contentWidth - CardPad * 2 - gap * (rowLines.size - 1).coerceAtLeast(0)
                    var x = Margin + CardPad
                    rowLines.forEachIndexed { index, line ->
                        val colW = usable * (weights.getOrElse(index) { 1f } / weightSum)
                        canvas!!.drawText(
                            line.label.uppercase(Locale.FRANCE),
                            x,
                            rowY + labelPaint.textSize,
                            labelPaint,
                        )
                        val values = wrapText(line.value, valueP, colW)
                        var vy = rowY + labelPaint.fontSpacing + 1f
                        values.take(2).forEach { v ->
                            canvas!!.drawText(v, x, vy + valueP.textSize, valueP)
                            vy += valueP.fontSpacing
                        }
                        x += colW + gap
                    }
                    rowY += rowH
                }
            }
            y += height + 6f
        }

        private fun drawKeyValueCard(lines: List<RecapLine>, labelRatio: Float = 0.28f) {
            if (lines.isEmpty()) return
            val labelW = contentWidth * labelRatio
            val valueW = (contentWidth - CardPad * 2 - labelW).coerceAtLeast(40f)
            val rowGap = 4f
            val prepared = lines.map { line ->
                val labelLines = wrapText(
                    line.label.uppercase(Locale.FRANCE),
                    labelPaint,
                    labelW,
                )
                val valueLines = wrapText(line.value, valuePaint, valueW)
                val lineCount = max(labelLines.size, valueLines.size).coerceAtLeast(1)
                Triple(labelLines, valueLines, lineCount * valuePaint.fontSpacing)
            }
            val height = CardPad * 2 + prepared.fold(0f) { acc, row -> acc + row.third } +
                rowGap * (prepared.size - 1).coerceAtLeast(0)
            ensureSpace(height + 6f)
            if (!dryRun) {
                val rect = RectF(Margin, y, Margin + contentWidth, y + height)
                fillPaint.color = ColorCardBg
                canvas!!.drawRoundRect(rect, Radius, Radius, fillPaint)
                strokePaint.color = ColorCardStroke
                canvas!!.drawRoundRect(rect, Radius, Radius, strokePaint)
                var rowY = y + CardPad
                prepared.forEach { (labelLines, valueLines, rowH) ->
                    var ly = rowY
                    labelLines.forEach { text ->
                        canvas!!.drawText(
                            text,
                            Margin + CardPad,
                            ly + labelPaint.textSize,
                            labelPaint,
                        )
                        ly += labelPaint.fontSpacing
                    }
                    var vy = rowY
                    valueLines.forEach { text ->
                        canvas!!.drawText(
                            text,
                            Margin + CardPad + labelW,
                            vy + valuePaint.textSize,
                            valuePaint,
                        )
                        vy += valuePaint.fontSpacing
                    }
                    rowY += rowH + rowGap
                }
            }
            y += height + 6f
        }

        private fun toneColors(tone: RecapCellTone): Pair<Int, Int>? = when (tone) {
            RecapCellTone.None -> null
            RecapCellTone.Green -> ColorGreenBg to ColorGreenFg
            RecapCellTone.Yellow -> ColorYellowBg to ColorYellowFg
            RecapCellTone.Orange -> ColorOrangeBg to ColorOrangeFg
        }

        private fun drawMeasuresTable(table: RecapMeasuresTable) {
            val cols = table.columnHeaders.size.coerceAtLeast(1)
            val labelPaintRow = TextPaint(bodyPaint).apply {
                textSize = 7.5f
                textAlign = Paint.Align.LEFT
            }
            val measureLabels = table.rows.mapNotNull { (it as? RecapTableRow.Measure)?.label }
            val labelColW = measureLabels.maxOfOrNull { labelPaintRow.measureText(it) + 10f }
                ?.coerceIn(56f, contentWidth * 0.32f)
                ?: 90f
            val dataW = contentWidth - labelColW
            val colW = dataW / cols
            val rowH = 16f
            val headerHasDate = table.columnHeaders.any { it.contains(' ') }
            val headerH = if (headerHasDate) 26f else 18f

            ensureSpace(headerH + 2f)
            if (!dryRun) {
                fillPaint.color = ColorHeaderBg
                canvas!!.drawRect(Margin, y, Margin + contentWidth, y + headerH, fillPaint)
                strokePaint.color = ColorGrid
                canvas!!.drawRect(Margin, y, Margin + contentWidth, y + headerH, strokePaint)
                cellHeaderPaint.textAlign = Paint.Align.LEFT
                canvas!!.drawText(
                    context.getString(R.string.recap_table_measure_column),
                    Margin + 3f,
                    y + headerH * 0.62f,
                    cellHeaderPaint,
                )
                cellHeaderPaint.textAlign = Paint.Align.CENTER
                table.columnHeaders.forEachIndexed { i, header ->
                    val cx = Margin + labelColW + colW * i + colW / 2f
                    val parts = header.split(' ', limit = 2)
                    if (parts.size == 2) {
                        canvas!!.drawText(parts[0], cx, y + 10f, cellHeaderPaint)
                        canvas!!.drawText(parts[1], cx, y + 20f, cellHeaderPaint)
                    } else {
                        canvas!!.drawText(header, cx, y + headerH * 0.65f, cellHeaderPaint)
                    }
                }
            }
            y += headerH

            table.rows.forEach { row ->
                when (row) {
                    is RecapTableRow.SectionHeader -> {
                        ensureSpace(rowH + 1f)
                        if (!dryRun) {
                            fillPaint.color = ColorHeaderBg
                            canvas!!.drawRect(Margin, y, Margin + contentWidth, y + rowH, fillPaint)
                            val kind = PdfMaterialIcons.forMesureSectionTitle(row.title, mesureTitles)
                            var textX = Margin + 3f
                            if (kind != null) {
                                drawIcon(
                                    kind,
                                    Margin + 2f,
                                    y + (rowH - IconDrawSizeSmall) / 2f,
                                    IconDrawSizeSmall,
                                    IconRasterSizeSmall,
                                )
                                textX = Margin + IconDrawSizeSmall + 6f
                            }
                            val sub = TextPaint(sectionPaint).apply { textSize = 8.5f }
                            canvas!!.drawText(row.title, textX, y + rowH * 0.72f, sub)
                        }
                        y += rowH
                    }
                    is RecapTableRow.Measure -> {
                        ensureSpace(rowH + 1f)
                        if (!dryRun) {
                            strokePaint.color = ColorGrid
                            canvas!!.drawRect(Margin, y, Margin + contentWidth, y + rowH, strokePaint)
                            canvas!!.drawLine(
                                Margin + labelColW,
                                y,
                                Margin + labelColW,
                                y + rowH,
                                strokePaint,
                            )
                            canvas!!.drawText(
                                ellipsize(row.label, labelPaintRow, labelColW - 6f),
                                Margin + 3f,
                                y + rowH * 0.72f,
                                labelPaintRow,
                            )
                            row.cells.forEachIndexed { i, cell ->
                                val left = Margin + labelColW + colW * i
                                val right = left + colW
                                canvas!!.drawLine(left, y, left, y + rowH, strokePaint)
                                val text = cell.text.ifBlank { "—" }
                                val tones = toneColors(cell.tone)
                                if (tones != null) {
                                    fillPaint.color = tones.first
                                    canvas!!.drawRect(
                                        left + 0.5f,
                                        y + 0.5f,
                                        right - 0.5f,
                                        y + rowH - 0.5f,
                                        fillPaint,
                                    )
                                    cellPaint.color = tones.second
                                } else {
                                    cellPaint.color = ColorInk
                                }
                                canvas!!.drawText(
                                    ellipsize(text, cellPaint, colW - 3f),
                                    left + colW / 2f,
                                    y + rowH * 0.72f,
                                    cellPaint,
                                )
                            }
                        }
                        y += rowH
                    }
                }
            }
            strokePaint.color = ColorCardStroke
            y += 8f
        }

        private fun drawPhoto(file: File, index: Int) {
            drawWrapped(
                text = context.getString(R.string.pdf_photo_caption, index + 1),
                paint = mutedPaint,
                spacingAfter = 3f,
            )
            if (dryRun) {
                ensureSpace(120f)
                y += 120f
                return
            }
            val bitmap = InterventionPhotoStore.decodeForPdf(
                file = file,
                maxWidth = contentWidth.toInt(),
                maxHeight = MaxPhotoHeight.toInt(),
            )
            if (bitmap == null) {
                drawWrapped(context.getString(R.string.pdf_photo_missing), mutedPaint)
                return
            }
            ensureSpace(bitmap.height + 12f)
            fillPaint.color = ColorCardBg
            canvas!!.drawRoundRect(
                RectF(Margin - 2f, y - 2f, Margin + bitmap.width + 2f, y + bitmap.height + 2f),
                4f,
                4f,
                fillPaint,
            )
            canvas!!.drawBitmap(bitmap, Margin, y, null)
            y += bitmap.height + 10f
            bitmap.recycle()
        }

        private fun drawContent() {
            drawWrapped(context.getString(R.string.pdf_document_title), titlePaint, spacingAfter = 10f)

            drawSectionTitle(
                context.getString(R.string.pdf_section_secouriste),
                NoteSectionKind.Questionnaires,
                titleBold = false,
            )
            val profileLines = buildList {
                profile.displayName().takeIf { it.isNotEmpty() }?.let {
                    add(RecapLine(context.getString(R.string.pdf_label_name), it))
                }
                profile.contact.takeIf { it.isNotBlank() }?.let {
                    add(RecapLine(context.getString(R.string.pdf_label_contact), it))
                }
                profile.organisme.takeIf { it.isNotBlank() }?.let {
                    add(RecapLine(context.getString(R.string.pdf_label_organisme), it))
                }
                profile.competences.takeIf { it.isNotBlank() }?.let {
                    add(RecapLine(context.getString(R.string.pdf_label_competences), it))
                }
            }
            drawColumnsCard(
                lines = profileLines,
                emptyMessage = context.getString(R.string.pdf_profile_empty),
                backgroundColor = ColorCardWhite,
                strokeColor = ColorSecouristeStroke,
                strokeWidth = 0.5f,
                boldValues = false,
                columnsPerRow = 4,
            )

            if (recap.identityLines.isNotEmpty()) {
                drawSectionTitle(context.getString(R.string.recap_section_victime), NoteSectionKind.Victime)
                // Prénom NOM | Date | Âge | Coordonnées — date/âge plus étroits
                val weights = when (recap.identityLines.size) {
                    1 -> listOf(1f)
                    2 -> listOf(1.4f, 1f)
                    3 -> listOf(1.4f, 0.9f, 0.7f)
                    else -> listOf(1.4f, 0.95f, 0.65f, 1.4f)
                }
                drawColumnsCard(
                    lines = recap.identityLines,
                    columnWeights = weights,
                    boldValues = true,
                    columnsPerRow = 4,
                )
            }

            recap.measuresTable?.let { table ->
                drawSectionTitle(context.getString(R.string.block_mesures), NoteSectionKind.Mesures)
                drawMeasuresTable(table)
            }

            if (recap.questionnaires.isNotEmpty()) {
                drawSectionTitle(
                    context.getString(R.string.recap_section_questionnaires),
                    NoteSectionKind.Questionnaires,
                )
                recap.questionnaires.forEach { section ->
                    ensureSpace(16f)
                    if (!dryRun) {
                        canvas!!.drawText(section.title, Margin, y + sectionPaint.textSize, sectionPaint)
                    }
                    y += sectionPaint.fontSpacing + 3f
                    drawKeyValueCard(section.lines, labelRatio = 0.30f)
                }
            }

            recap.comment?.takeIf { it.isNotBlank() }?.let { comment ->
                drawSectionTitle(context.getString(R.string.block_commentaire), NoteSectionKind.Commentaire)
                val lines = wrapText(comment, bodyPaint, contentWidth - CardPad * 2)
                val height = CardPad * 2 + lines.size * bodyPaint.fontSpacing
                ensureSpace(height + 6f)
                if (!dryRun) {
                    val rect = RectF(Margin, y, Margin + contentWidth, y + height)
                    fillPaint.color = ColorCardBg
                    canvas!!.drawRoundRect(rect, Radius, Radius, fillPaint)
                    strokePaint.color = ColorCardStroke
                    canvas!!.drawRoundRect(rect, Radius, Radius, strokePaint)
                    var ty = y + CardPad
                    lines.forEach { line ->
                        canvas!!.drawText(line, Margin + CardPad, ty + bodyPaint.textSize, bodyPaint)
                        ty += bodyPaint.fontSpacing
                    }
                }
                y += height + 6f
            }

            val existingPhotos = photoFiles.filter { it.exists() && it.length() > 0L }
            if (existingPhotos.isNotEmpty()) {
                drawSectionTitle(context.getString(R.string.pdf_section_photos), NoteSectionKind.Photos)
                existingPhotos.forEachIndexed { index, file -> drawPhoto(file, index) }
            }

            drawSectionTitle(
                context.getString(DisclaimerResources.documentTitle),
                NoteSectionKind.Commentaire,
            )
            val disclaimer = context.getString(
                DisclaimerResources.documentBody,
                DisclaimerResources.currentYear(),
            )
            drawWrapped(disclaimer, mutedPaint, spacingAfter = 0f)
        }
    }

    private fun ellipsize(text: String, paint: TextPaint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        val ellipsis = "…"
        var end = text.length
        while (end > 0 && paint.measureText(text.take(end) + ellipsis) > maxWidth) {
            end--
        }
        return text.take(max(0, end)) + ellipsis
    }

    private fun wrapText(text: String, paint: TextPaint, maxWidth: Float): List<String> {
        if (text.isEmpty()) return listOf("")
        val result = ArrayList<String>()
        text.replace("\r\n", "\n").split('\n').forEach { paragraph ->
            if (paragraph.isEmpty()) {
                result.add("")
                return@forEach
            }
            var remaining = paragraph
            while (remaining.isNotEmpty()) {
                val count = paint.breakText(remaining, true, maxWidth, null)
                if (count <= 0) {
                    result.add(remaining.take(1))
                    remaining = remaining.drop(1)
                    continue
                }
                var breakAt = count
                if (count < remaining.length) {
                    val space = remaining.lastIndexOf(' ', count - 1)
                    if (space > 0) breakAt = space
                }
                result.add(remaining.substring(0, breakAt).trimEnd())
                remaining = remaining.substring(breakAt).trimStart()
            }
        }
        return result.ifEmpty { listOf("") }
    }
}
