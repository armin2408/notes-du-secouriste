package com.notesdusecouriste.feature.aidememoire.ui

import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mohamedrejeb.richeditor.annotation.ExperimentalRichTextApi
import kotlinx.coroutines.delay
import java.text.Normalizer
import com.mohamedrejeb.richeditor.model.DefaultImageLoader
import com.mohamedrejeb.richeditor.model.HeadingStyle
import com.mohamedrejeb.richeditor.model.ImageData
import com.mohamedrejeb.richeditor.model.ImageLoader
import com.mohamedrejeb.richeditor.model.LocalImageLoader
import com.mohamedrejeb.richeditor.model.RichTextState
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import com.mohamedrejeb.richeditor.ui.material3.RichText
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditor
import com.mohamedrejeb.richeditor.ui.material3.RichTextEditorDefaults

/**
 * Barre d'outils de formatage pour l'éditeur aide-mémoire (Compose Rich Editor).
 */
@Composable
internal fun AideMemoireRichEditorToolbar(
    richTextState: RichTextState,
    modifier: Modifier = Modifier,
) {
    val isBold = richTextState.currentSpanStyle.fontWeight == FontWeight.Bold
    val isItalic = richTextState.currentSpanStyle.fontStyle == FontStyle.Italic
    val isUnderline = richTextState.currentSpanStyle.textDecoration
        ?.contains(TextDecoration.Underline) == true
    val isH1 = richTextState.currentHeadingStyle == HeadingStyle.H1
    val isH2 = richTextState.currentHeadingStyle == HeadingStyle.H2
    val isBulletList = richTextState.isUnorderedList
    val isLink = richTextState.isLink
    var linkDialogOpen by remember { mutableStateOf(false) }

    if (linkDialogOpen) {
        AideMemoireLinkDialog(
            richTextState = richTextState,
            onDismiss = { linkDialogOpen = false },
        )
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        val scroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            FormatToolbarTextButton(
                label = "G",
                selected = isBold,
                fontWeight = FontWeight.Bold,
                onClick = {
                    richTextState.toggleSpanStyle(
                        SpanStyle(fontWeight = FontWeight.Bold),
                    )
                },
            )
            FormatToolbarTextButton(
                label = "I",
                selected = isItalic,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                onClick = {
                    richTextState.toggleSpanStyle(
                        SpanStyle(fontStyle = FontStyle.Italic),
                    )
                },
            )
            FormatToolbarTextButton(
                label = "S",
                selected = isUnderline,
                fontWeight = FontWeight.Bold,
                textDecoration = TextDecoration.Underline,
                onClick = {
                    richTextState.toggleSpanStyle(
                        SpanStyle(textDecoration = TextDecoration.Underline),
                    )
                },
            )
            FormatToolbarIconButton(
                icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
                contentDescription = "Liste à puces",
                selected = isBulletList,
                onClick = { richTextState.toggleUnorderedList() },
            )
            FormatToolbarTextButton(
                label = "Titre 1",
                selected = isH1,
                onClick = {
                    richTextState.setHeadingStyle(
                        if (isH1) HeadingStyle.Normal else HeadingStyle.H1,
                    )
                },
            )
            FormatToolbarTextButton(
                label = "Titre 2",
                selected = isH2,
                onClick = {
                    richTextState.setHeadingStyle(
                        if (isH2) HeadingStyle.Normal else HeadingStyle.H2,
                    )
                },
            )
            TextButton(
                onClick = { linkDialogOpen = true },
            ) {
                Text(
                    text = "Lien",
                    color = if (isLink) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                )
            }
        }
    }
}

@Composable
private fun AideMemoireLinkDialog(
    richTextState: RichTextState,
    onDismiss: () -> Unit,
) {
    val selection = richTextState.selection
    val hasSelection = !selection.collapsed
    val selectedText = remember(selection, richTextState.annotatedString.text) {
        if (hasSelection) {
            richTextState.annotatedString.text.substring(selection.min, selection.max)
        } else {
            ""
        }
    }
    val editingLink = richTextState.isLink

    var linkText by remember {
        mutableStateOf(
            when {
                editingLink -> richTextState.selectedLinkText.orEmpty()
                hasSelection -> selectedText
                else -> ""
            },
        )
    }
    var linkUrl by remember {
        mutableStateOf(
            when {
                editingLink -> richTextState.selectedLinkUrl.orEmpty()
                else -> ""
            },
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editingLink) "Modifier le lien" else "Insérer un lien") },
        text = {
            Column {
                if (hasSelection) {
                    Text(
                        text = "Texte : $selectedText",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                } else {
                    OutlinedTextField(
                        value = linkText,
                        onValueChange = { linkText = it },
                        label = { Text("Texte du lien") },
                        placeholder = { Text("Ex. Ouvrir le PDF") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = editingLink,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = linkUrl,
                    onValueChange = { linkUrl = it },
                    label = { Text("Adresse (URL)") },
                    placeholder = { Text("https://…") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val url = linkUrl.trim()
                    if (url.isBlank()) return@TextButton
                    when {
                        editingLink -> richTextState.updateLink(url)
                        hasSelection -> richTextState.addLinkToSelection(url)
                        else -> {
                            val text = linkText.trim().ifBlank { "lien" }
                            richTextState.addLink(text = text, url = url)
                        }
                    }
                    onDismiss()
                },
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
    )
}

@Composable
private fun FormatToolbarTextButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    fontWeight: FontWeight = FontWeight.Normal,
    fontStyle: FontStyle? = null,
    textDecoration: TextDecoration? = null,
) {
    val color = if (selected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    TextButton(onClick = onClick) {
        Text(
            text = label,
            fontWeight = if (selected && fontWeight == FontWeight.Normal) FontWeight.Bold else fontWeight,
            fontStyle = fontStyle,
            textDecoration = textDecoration,
            color = color,
        )
    }
}

@Composable
private fun FormatToolbarIconButton(
    icon: ImageVector,
    contentDescription: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(24.dp),
            tint = if (selected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
        )
    }
}

@OptIn(ExperimentalRichTextApi::class)
@Composable
internal fun AideMemoireRichTextEnvironment(
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val platformUriHandler = LocalUriHandler.current
    val imageLoader = remember(context) { AideMemoireAppImageLoader(context) }
    val linkHandler = remember(platformUriHandler, context) {
        object : UriHandler {
            override fun openUri(uri: String) {
                openAideMemoireLink(context, platformUriHandler, uri)
            }
        }
    }
    CompositionLocalProvider(
        LocalImageLoader provides imageLoader,
        LocalUriHandler provides linkHandler,
    ) {
        content()
    }
}

/**
 * Affichage lecture seule — même moteur de rendu que l'éditeur ([RichText] / Compose Rich Editor).
 * La recherche réutilise ce moteur et surligne les occurrences sur le texte rendu.
 */
@OptIn(ExperimentalRichTextApi::class)
@Composable
internal fun AideMemoireRichTextViewer(
    markdown: String,
    modifier: Modifier = Modifier,
    emptyText: String = "Onglet vide.",
    searchQuery: String? = null,
    requestedMatchIndex: Int? = null,
    searchScrollNonce: Int = 0,
    onMatchCountChanged: (Int) -> Unit = {},
) {
    val richTextState = rememberRichTextState()
    val linkColor = MaterialTheme.colorScheme.primary
    val highlightColor = MaterialTheme.colorScheme.tertiaryContainer
    val activeHighlightColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f)
    val normalizedMarkdown = remember(markdown) { markdown.replace("\r\n", "\n") }
    val normalizedQuery = searchQuery?.trim().orEmpty()
    var textLayoutResult by remember { mutableStateOf<TextLayoutResult?>(null) }
    val scrollState = rememberScrollState()

    LaunchedEffect(normalizedMarkdown, linkColor) {
        richTextState.config.linkColor = linkColor
        richTextState.config.linkTextDecoration = TextDecoration.Underline
        richTextState.setMarkdown(normalizedMarkdown)
    }

    val plainText = richTextState.annotatedString.text
    val matchRanges = remember(plainText, normalizedQuery) {
        if (normalizedQuery.isBlank()) {
            emptyList()
        } else {
            val textMap = normalizeAideMemoireSearchTextWithMap(plainText)
            val query = normalizeAideMemoireSearchText(normalizedQuery)
            findAideMemoireSearchOccurrences(textMap, query)
                .filter { range -> range.first < range.last + 1 && range.last < plainText.length }
        }
    }

    LaunchedEffect(matchRanges.size) {
        onMatchCountChanged(matchRanges.size)
    }

    val clampedMatchIndex = remember(requestedMatchIndex, matchRanges.size) {
        requestedMatchIndex?.coerceIn(0, (matchRanges.size - 1).coerceAtLeast(0))
    }

    LaunchedEffect(clampedMatchIndex, searchScrollNonce, matchRanges, textLayoutResult) {
        val index = clampedMatchIndex ?: return@LaunchedEffect
        val range = matchRanges.getOrNull(index) ?: return@LaunchedEffect
        val layout = textLayoutResult ?: return@LaunchedEffect
        delay(80)
        val targetY = layout.getBoundingBox(range.first).top.toInt()
        scrollState.animateScrollTo(targetY.coerceAtLeast(0))
    }

    AideMemoireRichTextEnvironment {
        if (normalizedMarkdown.isBlank()) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        } else {
            Column(
                modifier = modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .imePadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
            ) {
                if (normalizedQuery.isNotBlank()) {
                    Text(
                        text = if (matchRanges.isEmpty()) {
                            "Aucun résultat."
                        } else {
                            "${matchRanges.size} résultat(s)."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }
                RichText(
                    state = richTextState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawBehind {
                            if (normalizedQuery.isBlank()) return@drawBehind
                            val layout = textLayoutResult ?: return@drawBehind
                            matchRanges.forEachIndexed { index, range ->
                                drawAideMemoireSearchHighlight(
                                    layout = layout,
                                    range = range,
                                    color = if (index == clampedMatchIndex) {
                                        activeHighlightColor
                                    } else {
                                        highlightColor
                                    },
                                )
                            }
                        },
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                    onTextLayout = { layout -> textLayoutResult = layout },
                )
            }
        }
    }
}

private fun DrawScope.drawAideMemoireSearchHighlight(
    layout: TextLayoutResult,
    range: IntRange,
    color: Color,
) {
    val textLength = layout.layoutInput.text.length
    val start = range.first.coerceIn(0, textLength)
    val end = (range.last + 1).coerceIn(start, textLength)
    if (start >= end) return

    val startLine = layout.getLineForOffset(start)
    val endLine = layout.getLineForOffset((end - 1).coerceAtLeast(start))
    for (line in startLine..endLine) {
        val lineStart = layout.getLineStart(line)
        val lineEnd = layout.getLineEnd(line)
        val highlightStart = maxOf(start, lineStart)
        val highlightEnd = minOf(end, lineEnd)
        if (highlightStart >= highlightEnd) continue

        val left = layout.getHorizontalPosition(highlightStart, usePrimaryDirection = true)
        val right = if (highlightEnd >= lineEnd) {
            layout.getLineRight(line)
        } else {
            layout.getHorizontalPosition(highlightEnd, usePrimaryDirection = true)
        }
        val top = layout.getLineTop(line)
        val bottom = layout.getLineBottom(line)
        drawRect(
            color = color,
            topLeft = Offset(left, top),
            size = Size((right - left).coerceAtLeast(0f), bottom - top),
        )
    }
}

private data class AideMemoireNormalizedTextMap(
    val normalized: String,
    val indexMap: IntArray,
) {
    fun mapToOriginal(normalizedIndex: Int): Int? =
        if (normalizedIndex < 0 || normalizedIndex >= indexMap.size) null else indexMap[normalizedIndex]
}

private fun normalizeAideMemoireSearchText(input: String): String {
    val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
    return normalized.replace(Regex("\\p{Mn}+"), "").lowercase()
}

private fun normalizeAideMemoireSearchTextWithMap(input: String): AideMemoireNormalizedTextMap {
    val sb = StringBuilder(input.length)
    val map = ArrayList<Int>(input.length)

    input.forEachIndexed { originalIndex, ch ->
        val base = Normalizer.normalize(ch.toString(), Normalizer.Form.NFD)
            .replace(Regex("\\p{Mn}+"), "")
            .lowercase()
        base.forEach { outCh ->
            sb.append(outCh)
            map.add(originalIndex)
        }
    }

    return AideMemoireNormalizedTextMap(
        normalized = sb.toString(),
        indexMap = map.toIntArray(),
    )
}

private fun findAideMemoireSearchOccurrences(
    textMap: AideMemoireNormalizedTextMap,
    normalizedQuery: String,
): List<IntRange> {
    if (normalizedQuery.isBlank()) return emptyList()
    val out = ArrayList<IntRange>()
    var idx = textMap.normalized.indexOf(normalizedQuery, startIndex = 0)
    while (idx >= 0) {
        val endIdx = idx + normalizedQuery.length
        val startOriginal = textMap.mapToOriginal(idx) ?: break
        val endOriginal = textMap.mapToOriginal(endIdx - 1)?.plus(1) ?: break
        if (endOriginal > startOriginal) {
            out.add(startOriginal until endOriginal)
        }
        idx = textMap.normalized.indexOf(normalizedQuery, startIndex = idx + normalizedQuery.length)
    }
    return out
}

@OptIn(ExperimentalRichTextApi::class)
private class AideMemoireAppImageLoader(
    private val context: Context,
) : ImageLoader {
    @Composable
    override fun load(model: Any): ImageData? {
        val url = model as? String ?: return DefaultImageLoader.load(model)
        if (!url.startsWith("appimg://")) {
            return DefaultImageLoader.load(model)
        }
        val token = url.removePrefix("appimg://").trim()
        val imageBitmap = remember(token) { loadLocalImageBitmap(context, token) } ?: return null
        return ImageData(
            painter = BitmapPainter(imageBitmap),
            contentDescription = "Image",
        )
    }
}

/**
 * Éditeur WYSIWYG aide-mémoire basé sur [Compose Rich Editor](https://github.com/MohamedRejeb/compose-rich-editor).
 * Le contenu est chargé / exporté en Markdown pour rester compatible avec le viewer existant.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AideMemoireRichTextEditor(
    richTextState: RichTextState,
    modifier: Modifier = Modifier,
) {
    val background = MaterialTheme.colorScheme.background
    val editorColors = RichTextEditorDefaults.richTextEditorColors(
        containerColor = background,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        placeholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )

    AideMemoireRichTextEnvironment {
        RichTextEditor(
            state = richTextState,
            modifier = modifier
                .fillMaxSize()
                .imePadding()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MaterialTheme.colorScheme.onSurface,
            ),
            placeholder = {
                Text(
                    text = "Saisir le contenu…",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            colors = editorColors,
        )
    }
}
