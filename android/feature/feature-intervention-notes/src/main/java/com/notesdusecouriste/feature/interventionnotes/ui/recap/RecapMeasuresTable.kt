package com.notesdusecouriste.feature.interventionnotes.ui.recap

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.notesdusecouriste.feature.interventionnotes.R

private val MeasureColumnWidth = 168.dp
private val TimeColumnWidthCompact = 88.dp
private val TimeColumnMinWidthExpanded = 88.dp
/** Texte nu : padding horizontal du `TableValueText` (8+8) + marge cellule. */
private val TimeColumnHorizontalPaddingExpanded = 28.dp
/** Supplément puce : `Surface` (4+4) + `Box` interne (8+8) au-delà du texte seul. */
private val TimeColumnPillExtraHorizontalPadding = 24.dp
private val HeaderRowHeight = 44.dp
private val SubtitleRowHeight = 40.dp
private val MeasureRowHeight = 52.dp
private val TableCellFontSize = 17.sp

@Composable
internal fun rememberRecapTimeColumnWidths(
    table: RecapMeasuresTable,
    columnsExpanded: Boolean,
    emptyCellLabel: String,
): List<Dp> {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val headerStyle = MaterialTheme.typography.titleSmall.copy(
        fontWeight = FontWeight.SemiBold,
        fontSize = TableCellFontSize,
    )
    val bodyStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = TableCellFontSize)

    return remember(
        table.columnHeaders,
        table.rows,
        columnsExpanded,
        emptyCellLabel,
        headerStyle,
        bodyStyle,
    ) {
        if (!columnsExpanded) {
            List(table.columnHeaders.size) { TimeColumnWidthCompact }
        } else {
            val plainPaddingPx = with(density) { TimeColumnHorizontalPaddingExpanded.roundToPx() }
            val pillPaddingPx = with(density) {
                (TimeColumnHorizontalPaddingExpanded + TimeColumnPillExtraHorizontalPadding).roundToPx()
            }
            table.columnHeaders.indices.map { columnIndex ->
                var maxWidthPx = with(density) { TimeColumnMinWidthExpanded.roundToPx() }

                val headerText = table.columnHeaders[columnIndex]
                maxWidthPx = maxOf(
                    maxWidthPx,
                    textMeasurer.measure(
                        text = headerText,
                        style = headerStyle,
                        softWrap = false,
                        maxLines = 1,
                    ).size.width + plainPaddingPx,
                )

                table.rows.forEach { row ->
                    if (row is RecapTableRow.Measure) {
                        val cell = row.cells.getOrElse(columnIndex) { RecapTableCell(emptyCellLabel) }
                        if (cell.text.isNotBlank() && cell.text != emptyCellLabel) {
                            val textWidthPx = textMeasurer.measure(
                                text = cell.text,
                                style = bodyStyle,
                                softWrap = false,
                                maxLines = 1,
                            ).size.width
                            val cellPaddingPx = if (cell.tone != RecapCellTone.None) {
                                pillPaddingPx
                            } else {
                                plainPaddingPx
                            }
                            maxWidthPx = maxOf(maxWidthPx, textWidthPx + cellPaddingPx)
                        }
                    }
                }

                with(density) { maxWidthPx.toDp() }
            }
        }
    }
}

@Composable
fun RecapMeasuresTableHeaderRow(
    table: RecapMeasuresTable,
    horizontalScrollState: ScrollState,
    columnsExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val emptyCell = stringResource(R.string.recap_table_empty_cell)
    val columnWidths = rememberRecapTimeColumnWidths(
        table = table,
        columnsExpanded = columnsExpanded,
        emptyCellLabel = emptyCell,
    )
    Column(
        modifier = modifier.background(MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .height(HeaderRowHeight),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .width(MeasureColumnWidth)
                    .height(HeaderRowHeight),
            )
            table.columnHeaders.forEachIndexed { index, header ->
                if (index > 0) {
                    TableVerticalDivider()
                }
                TableValueCell(
                    text = header,
                    isHeader = true,
                    height = HeaderRowHeight,
                    columnWidth = columnWidths[index],
                    toneColors = null,
                )
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}

@Composable
fun RecapMeasuresTableBody(
    table: RecapMeasuresTable,
    horizontalScrollState: ScrollState,
    columnsExpanded: Boolean,
    modifier: Modifier = Modifier,
) {
    val emptyCell = stringResource(R.string.recap_table_empty_cell)
    val columnWidths = rememberRecapTimeColumnWidths(
        table = table,
        columnsExpanded = columnsExpanded,
        emptyCellLabel = emptyCell,
    )
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(
            topStart = 0.dp,
            topEnd = 0.dp,
            bottomStart = 16.dp,
            bottomEnd = 16.dp,
        ),
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Row(
            modifier = Modifier
                .horizontalScroll(horizontalScrollState)
                .height(IntrinsicSize.Min),
        ) {
            MeasureLabelColumnBody(rows = table.rows)
            table.columnHeaders.forEachIndexed { index, _ ->
                if (index > 0) {
                    TableVerticalDivider()
                }
                TimeValueColumnBody(
                    columnIndex = index,
                    columnWidth = columnWidths[index],
                    rows = table.rows,
                    emptyCell = emptyCell,
                )
            }
        }
    }
}

@Composable
private fun MeasureLabelColumnBody(rows: List<RecapTableRow>) {
    Column(modifier = Modifier.width(MeasureColumnWidth)) {
        rows.forEach { row ->
            if (row != rows.first()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            when (row) {
                is RecapTableRow.SectionHeader -> TableSubtitleCell(
                    text = row.title,
                    height = SubtitleRowHeight,
                )
                is RecapTableRow.Measure -> TableLabelCell(
                    text = row.label,
                    height = MeasureRowHeight,
                )
            }
        }
    }
}

@Composable
private fun TimeValueColumnBody(
    columnIndex: Int,
    columnWidth: Dp,
    rows: List<RecapTableRow>,
    emptyCell: String,
) {
    Column(modifier = Modifier.width(columnWidth)) {
        rows.forEach { row ->
            if (row != rows.first()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            }
            when (row) {
                is RecapTableRow.SectionHeader -> TableValueCell(
                    text = "",
                    isHeader = false,
                    height = SubtitleRowHeight,
                    columnWidth = columnWidth,
                    toneColors = null,
                )
                is RecapTableRow.Measure -> {
                    val cell = row.cells.getOrElse(columnIndex) { RecapTableCell(emptyCell) }
                    val toneColors = cell.tone.toColors()
                    TableValueCell(
                        text = cell.text,
                        isHeader = false,
                        height = MeasureRowHeight,
                        columnWidth = columnWidth,
                        toneColors = if (cell.text != emptyCell) toneColors else null,
                    )
                }
            }
        }
    }
}

@Composable
private fun TableLabelCell(
    text: String,
    height: Dp,
) {
    Box(
        modifier = Modifier
            .width(MeasureColumnWidth)
            .height(height)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = TableCellFontSize),
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TableSubtitleCell(
    text: String,
    height: Dp,
) {
    Box(
        modifier = Modifier
            .width(MeasureColumnWidth)
            .height(height)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = TableCellFontSize,
            ),
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TableValueCell(
    text: String,
    isHeader: Boolean,
    height: Dp,
    columnWidth: Dp,
    toneColors: RecapToneColors?,
) {
    val columnModifier = Modifier
        .width(columnWidth)
        .height(height)
        .then(
            if (isHeader) {
                Modifier.background(MaterialTheme.colorScheme.surfaceContainerLow)
            } else {
                Modifier
            },
        )

    Box(
        modifier = columnModifier,
        contentAlignment = Alignment.Center,
    ) {
        if (toneColors != null && !isHeader) {
            Surface(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                shape = MaterialTheme.shapes.small,
                color = toneColors.container,
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    TableValueText(
                        text = text,
                        isHeader = false,
                        color = toneColors.onContainer,
                    )
                }
            }
        } else {
            TableValueText(
                text = text,
                isHeader = isHeader,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun TableValueText(
    text: String,
    isHeader: Boolean,
    color: Color,
) {
    val style = if (isHeader) {
        MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.SemiBold,
            fontSize = TableCellFontSize,
        )
    } else {
        MaterialTheme.typography.bodyMedium.copy(fontSize = TableCellFontSize)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = style,
            color = color,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun TableVerticalDivider() {
    VerticalDivider(
        modifier = Modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}
