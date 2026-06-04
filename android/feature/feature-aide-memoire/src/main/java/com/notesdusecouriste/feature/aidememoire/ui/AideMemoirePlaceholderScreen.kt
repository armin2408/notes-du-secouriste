package com.notesdusecouriste.feature.aidememoire.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.graphics.BitmapFactory
import android.os.ParcelFileDescriptor
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DragHandle
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalDensity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.Normalizer
import java.util.UUID

private data class MatchPosition(
    val lineIndex: Int,
    val start: Int,
    val end: Int,
)

private data class NormalizedTextMap(
    val normalized: String,
    val indexMap: IntArray,
) {
    fun mapToOriginal(normalizedIndex: Int): Int? =
        if (normalizedIndex < 0 || normalizedIndex >= indexMap.size) null else indexMap[normalizedIndex]
}

private fun normalizeSearchTextWithMap(input: String): NormalizedTextMap {
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

    return NormalizedTextMap(
        normalized = sb.toString(),
        indexMap = map.toIntArray(),
    )
}

private fun findAllOccurrences(haystack: String, needle: String): List<Int> {
    if (needle.isBlank()) return emptyList()
    val out = ArrayList<Int>()
    var idx = haystack.indexOf(needle, startIndex = 0)
    while (idx >= 0) {
        out.add(idx)
        idx = haystack.indexOf(needle, startIndex = idx + needle.length)
    }
    return out
}

private data class AideMemoireSpan(
    val start: Int,
    val end: Int,
    val style: SpanStyle,
)

/**
 * WYSIWYG inline minimaliste pour l'éditeur Aide‑mémoire.
 * On conserve le texte markdown "source", mais on masque les marqueurs et on applique un style.
 *
 * Support (volontairement limité pour rester stable) :
 * - Titres: `# ` / `## ` / `### ` au début d'une ligne (marqueurs masqués, texte en semi-gras)
 * - Liste: `- ` au début d'une ligne (affiché `• `)
 * - Gras: `**texte**` (marqueurs masqués)
 * - Italique: `_texte_` (heuristique simple, marqueurs masqués)
 */
private class AideMemoireWysiwygMarkdownTransformation(
    private val h1Style: SpanStyle,
    private val h2Style: SpanStyle,
    private val h3Style: SpanStyle,
    private val bulletMarkerStyle: SpanStyle,
) : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val src = text.text
        val out = StringBuilder(src.length)

        val origToTrans = IntArray(src.length + 1)
        val transToOrig = ArrayList<Int>(src.length + 1)
        transToOrig.add(0)

        fun appendChar(ch: Char, originIndex: Int) {
            out.append(ch)
            transToOrig.add(originIndex + 1)
        }

        fun setMapForOrigin(originPos: Int, mappedTransPos: Int) {
            if (originPos in 0..src.length) origToTrans[originPos] = mappedTransPos
        }

        val spans = ArrayList<AideMemoireSpan>()

        var i = 0
        var transIndex = 0

        while (i < src.length) {
            setMapForOrigin(i, transIndex)

            val lineStart = (i == 0 || src[i - 1] == '\n')
            if (lineStart) {
                val h3 = src.startsWith("### ", i)
                val h2 = src.startsWith("## ", i)
                val h1 = src.startsWith("# ", i)
                if (h3 || h2 || h1) {
                    val markerLen = if (h3) 4 else if (h2) 3 else 2
                    for (m in 0 until markerLen) setMapForOrigin(i + m, transIndex)
                    i += markerLen

                    val spanStart = transIndex
                    while (i < src.length && src[i] != '\n') {
                        setMapForOrigin(i, transIndex)
                        appendChar(src[i], i)
                        i++
                        transIndex++
                    }
                    val style = if (h1) h1Style else if (h2) h2Style else h3Style
                    spans.add(AideMemoireSpan(spanStart, transIndex, style))
                    continue
                }

                if (src.startsWith("- ", i)) {
                    setMapForOrigin(i, transIndex)
                    setMapForOrigin(i + 1, transIndex)
                    val bulletStart = transIndex
                    appendChar('•', i)
                    transIndex++
                    appendChar(' ', i + 1)
                    transIndex++
                    spans.add(AideMemoireSpan(bulletStart, bulletStart + 1, bulletMarkerStyle))
                    i += 2
                    continue
                }
            }

            if (src.startsWith("**", i)) {
                val close = src.indexOf("**", startIndex = i + 2)
                if (close > i + 2) {
                    setMapForOrigin(i, transIndex)
                    setMapForOrigin(i + 1, transIndex)
                    i += 2
                    val spanStart = transIndex
                    while (i < close) {
                        setMapForOrigin(i, transIndex)
                        appendChar(src[i], i)
                        i++
                        transIndex++
                    }
                    spans.add(AideMemoireSpan(spanStart, transIndex, SpanStyle(fontWeight = FontWeight.Bold)))
                    setMapForOrigin(close, transIndex)
                    setMapForOrigin(close + 1, transIndex)
                    i = close + 2
                    continue
                }
            }

            if (src[i] == '_') {
                val close = src.indexOf('_', startIndex = i + 1)
                if (close > i + 1) {
                    val prevOk = (i == 0) || src[i - 1].isWhitespace() || src[i - 1] == '\n'
                    val nextOk = (close + 1 >= src.length) || src[close + 1].isWhitespace() || src[close + 1] == '\n'
                    if (prevOk && nextOk) {
                        setMapForOrigin(i, transIndex)
                        i += 1
                        val spanStart = transIndex
                        while (i < close) {
                            setMapForOrigin(i, transIndex)
                            appendChar(src[i], i)
                            i++
                            transIndex++
                        }
                        spans.add(AideMemoireSpan(spanStart, transIndex, SpanStyle(fontStyle = FontStyle.Italic)))
                        setMapForOrigin(close, transIndex)
                        i = close + 1
                        continue
                    }
                }
            }

            appendChar(src[i], i)
            i++
            transIndex++
        }

        setMapForOrigin(src.length, transIndex)

        val transformed = buildAnnotatedString {
            append(out.toString())
            spans.forEach { s ->
                val safeStart = s.start.coerceIn(0, length)
                val safeEnd = s.end.coerceIn(0, length)
                if (safeEnd > safeStart) addStyle(s.style, safeStart, safeEnd)
            }
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int =
                origToTrans[offset.coerceIn(0, origToTrans.lastIndex)].coerceIn(0, transformed.length)

            override fun transformedToOriginal(offset: Int): Int {
                val safe = offset.coerceIn(0, transformed.length)
                val mapped = transToOrig.getOrNull(safe) ?: (src.length + 1)
                return (mapped - 1).coerceIn(0, src.length)
            }
        }

        return TransformedText(transformed, offsetMapping)
    }
}

@Composable
private fun AideMemoireEditorToolbar(
    onH1: () -> Unit,
    onH2: () -> Unit,
    onBullets: () -> Unit,
    onBold: () -> Unit,
    onItalic: () -> Unit,
    onLink: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        val scroll = rememberScrollState()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scroll)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onH1) { Text("H1") }
            TextButton(onClick = onH2) { Text("H2") }
            TextButton(onClick = onBullets) { Text("•") }
            TextButton(onClick = onBold) { Text("B") }
            TextButton(onClick = onItalic) { Text("I") }
            TextButton(onClick = onLink) { Text("Lien") }
        }
    }
}

private fun wrapSelection(value: TextFieldValue, prefix: String, suffix: String): TextFieldValue {
    val selStart = value.selection.start.coerceIn(0, value.text.length)
    val selEnd = value.selection.end.coerceIn(0, value.text.length)
    val before = value.text.substring(0, selStart)
    val middle = value.text.substring(selStart, selEnd)
    val after = value.text.substring(selEnd)
    val nextText = before + prefix + middle + suffix + after
    val cursor = (selEnd + prefix.length + suffix.length).coerceIn(0, nextText.length)
    return value.copy(
        text = nextText,
        selection = TextRange(cursor, cursor),
    )
}

private fun insertLink(value: TextFieldValue): TextFieldValue {
    val selStart = value.selection.start.coerceIn(0, value.text.length)
    val selEnd = value.selection.end.coerceIn(0, value.text.length)
    val selected = value.text.substring(selStart, selEnd).ifBlank { "texte" }
    val insertion = "[$selected](https://)"
    val before = value.text.substring(0, selStart)
    val after = value.text.substring(selEnd)
    val nextText = before + insertion + after
    val cursor = (before.length + insertion.length - 1).coerceIn(0, nextText.length) // placer avant ')'
    return value.copy(text = nextText, selection = TextRange(cursor, cursor))
}

private fun insertPrefixAtLineStart(value: TextFieldValue, prefix: String): TextFieldValue {
    val cursor = value.selection.start.coerceIn(0, value.text.length)
    val lineStart = value.text.lastIndexOf('\n', startIndex = (cursor - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
    val before = value.text.substring(0, lineStart)
    val after = value.text.substring(lineStart)
    val nextText = before + prefix + after
    val nextCursor = (cursor + prefix.length).coerceIn(0, nextText.length)
    return value.copy(text = nextText, selection = TextRange(nextCursor, nextCursor))
}

private fun prefixSelectedLines(value: TextFieldValue, prefix: String): TextFieldValue {
    val text = value.text
    val start = value.selection.min.coerceIn(0, text.length)
    val end = value.selection.max.coerceIn(0, text.length)

    val selStartLine = text.lastIndexOf('\n', startIndex = (start - 1).coerceAtLeast(0)).let { if (it < 0) 0 else it + 1 }
    val selEndLineEnd = text.indexOf('\n', startIndex = end).let { if (it < 0) text.length else it }
    val block = text.substring(selStartLine, selEndLineEnd)
    val lines = block.split('\n')
    val updatedBlock = lines.joinToString("\n") { line ->
        if (line.isBlank()) line else prefix + line
    }
    val nextText = text.substring(0, selStartLine) + updatedBlock + text.substring(selEndLineEnd)
    val delta = updatedBlock.length - block.length
    val nextSelectionEnd = (value.selection.end + delta).coerceIn(0, nextText.length)
    return value.copy(text = nextText, selection = TextRange(nextSelectionEnd, nextSelectionEnd))
}

@Composable
private fun AideMemoireSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    matchCount: Int,
    currentMatchIndex: Int,
    onPreviousMatch: () -> Unit,
    onNextMatch: () -> Unit,
    onClose: () -> Unit,
) {
    val hasResults = matchCount > 0
    val barColor = MaterialTheme.colorScheme.surfaceContainer
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = barColor,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                placeholder = { Text("Rechercher…") },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = barColor,
                    unfocusedContainerColor = barColor,
                    disabledContainerColor = barColor,
                ),
            )
            IconButton(
                onClick = onPreviousMatch,
                enabled = hasResults,
            ) {
                Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Résultat précédent")
            }
            IconButton(
                onClick = onNextMatch,
                enabled = hasResults,
            ) {
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Résultat suivant")
            }
            IconButton(onClick = onClose) {
                Icon(Icons.Outlined.Close, contentDescription = "Fermer la recherche")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AideMemoirePlaceholderScreen(
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repository = remember { AideMemoireTabsRepository(context) }

    val tabs by repository.tabs.collectAsState(initial = emptyList())
    val visibleTabs = remember(tabs) { tabs.filter { it.visible } }

    var selectedTabId by remember { mutableStateOf<String?>(null) }
    val selectedTabIndex = remember(visibleTabs, selectedTabId) {
        val idx = visibleTabs.indexOfFirst { it.id == selectedTabId }
        if (idx >= 0) idx else 0
    }

    LaunchedEffect(visibleTabs) {
        selectedTabId = visibleTabs.getOrNull(selectedTabIndex)?.id
    }

    var manageDialogOpen by remember { mutableStateOf(false) } // legacy: replaced by dedicated screen
    var manageScreenOpen by remember { mutableStateOf(false) }
    var overflowMenuOpen by remember { mutableStateOf(false) }

    var createDialogOpen by remember { mutableStateOf(false) }
    var newTabTitle by remember { mutableStateOf("") }

    var searchMode by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchNavIndex by remember { mutableIntStateOf(0) } // index dans la liste des résultats
    var searchMatchCount by remember { mutableIntStateOf(0) }
    var searchScrollNonce by remember { mutableIntStateOf(0) } // re-scroll même résultat (ex. 1 seul hit)
    val focusManager = LocalFocusManager.current
    var editMode by remember { mutableStateOf(false) }
    var editBuffer by remember { mutableStateOf("") }
    var editOriginal by remember { mutableStateOf("") }
    var editValue by remember { mutableStateOf(TextFieldValue("")) }
    var confirmDiscardEditsOpen by remember { mutableStateOf(false) }
    var pdfNoViewerDialogOpen by remember { mutableStateOf(false) }

    val selectedTab = visibleTabs.getOrNull(selectedTabIndex)
    val isSelectedCustomMarkdown = selectedTab?.type == AideMemoireTabType.Markdown && selectedTab?.isDefault == false

    LaunchedEffect(selectedTab?.type) {
        if (selectedTab?.type != AideMemoireTabType.Markdown && searchMode) {
            searchMode = false
            searchQuery = ""
            searchNavIndex = 0
            searchMatchCount = 0
        }
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri == null) return@rememberLauncherForActivityResult
        val token = runCatching { copyImageIntoApp(context, uri) }.getOrNull() ?: return@rememberLauncherForActivityResult
        val insertion = "\n![](appimg://$token)\n"
        editBuffer += insertion
    }

    if (manageScreenOpen) {
        ManageTabsScreen(
            tabs = tabs,
            onBack = { manageScreenOpen = false },
            onMove = { fromIndex, toIndex ->
                scope.launch { repository.moveToIndex(fromIndex = fromIndex, toIndex = toIndex) }
            },
            onToggleVisible = { id -> scope.launch { repository.toggleVisible(id) } },
            onDuplicate = { id -> scope.launch { repository.duplicate(id) } },
            onDelete = { id -> scope.launch { repository.deleteCustom(id) } },
            onCreateNew = {
                newTabTitle = ""
                createDialogOpen = true
            },
        )
        // L'écran de gestion remplace le reste.
        if (createDialogOpen) {
            CreateTabDialog(
                title = newTabTitle,
                onTitleChange = { newTabTitle = it },
                onDismiss = { createDialogOpen = false },
                onCreate = {
                    val safeTitle = newTabTitle.trim().ifBlank { "Nouvel onglet" }
                    scope.launch { repository.createBlankMarkdown(title = safeTitle) }
                    createDialogOpen = false
                },
            )
        }
        return
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aide mémoire") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    actions()

                    if (selectedTab != null && selectedTab.type == AideMemoireTabType.Markdown) {
                        IconButton(onClick = { searchMode = true }) {
                            Icon(Icons.Outlined.Search, contentDescription = "Rechercher")
                        }
                    }

                    if (isSelectedCustomMarkdown) {
                        if (!editMode) {
                            IconButton(
                                onClick = {
                                    editMode = true
                                    editOriginal = selectedTab.markdown ?: ""
                                    editBuffer = editOriginal
                                    editValue = TextFieldValue(
                                        text = editOriginal,
                                        selection = TextRange(editOriginal.length),
                                    )
                                },
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = "Modifier")
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    if (editBuffer != editOriginal) confirmDiscardEditsOpen = true else editMode = false
                                },
                            ) {
                                Icon(Icons.Outlined.Close, contentDescription = "Annuler")
                            }
                            IconButton(
                                onClick = {
                                    val id = selectedTab.id
                                    scope.launch { repository.updateMarkdown(id, editBuffer) }
                                    editMode = false
                                },
                            ) {
                                Icon(Icons.Outlined.Save, contentDescription = "Enregistrer")
                            }
                            IconButton(
                                onClick = { imagePicker.launch("image/*") },
                            ) {
                                Icon(Icons.Outlined.Image, contentDescription = "Insérer une image")
                            }
                        }
                    }

                    Box {
                        IconButton(onClick = { overflowMenuOpen = true }) {
                            Icon(Icons.Outlined.MoreVert, contentDescription = "Menu")
                        }
                        DropdownMenu(
                            expanded = overflowMenuOpen,
                            onDismissRequest = { overflowMenuOpen = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Gérer les onglets") },
                                onClick = {
                                    overflowMenuOpen = false
                                    manageScreenOpen = true
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Nouvel onglet") },
                                leadingIcon = { Icon(Icons.Outlined.Add, contentDescription = null) },
                                onClick = {
                                    overflowMenuOpen = false
                                    newTabTitle = ""
                                    createDialogOpen = true
                                },
                            )
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
        ) {
            if (visibleTabs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Aucun onglet visible.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                if (
                    searchMode &&
                    selectedTab != null &&
                    selectedTab.type == AideMemoireTabType.Markdown
                ) {
                    AideMemoireSearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            searchNavIndex = 0
                        },
                        matchCount = searchMatchCount,
                        currentMatchIndex = searchNavIndex,
                        onPreviousMatch = {
                            if (searchMatchCount > 0) {
                                focusManager.clearFocus()
                                if (searchNavIndex > 0) {
                                    searchNavIndex -= 1
                                } else {
                                    searchScrollNonce++
                                }
                            }
                        },
                        onNextMatch = {
                            if (searchMatchCount > 0) {
                                focusManager.clearFocus()
                                if (searchNavIndex < searchMatchCount - 1) {
                                    searchNavIndex += 1
                                } else {
                                    searchScrollNonce++
                                }
                            }
                        },
                        onClose = {
                            searchMode = false
                            searchQuery = ""
                            searchNavIndex = 0
                            searchMatchCount = 0
                            searchScrollNonce = 0
                        },
                    )
                }

                if (editMode) {
                    AideMemoireEditorToolbar(
                        onH1 = {
                            editValue = prefixSelectedLines(editValue, "# ")
                            editBuffer = editValue.text
                        },
                        onH2 = {
                            editValue = prefixSelectedLines(editValue, "## ")
                            editBuffer = editValue.text
                        },
                        onBullets = {
                            editValue = prefixSelectedLines(editValue, "- ")
                            editBuffer = editValue.text
                        },
                        onBold = {
                            editValue = wrapSelection(editValue, "**", "**")
                            editBuffer = editValue.text
                        },
                        onItalic = {
                            editValue = wrapSelection(editValue, "_", "_")
                            editBuffer = editValue.text
                        },
                        onLink = {
                            editValue = insertLink(editValue)
                            editBuffer = editValue.text
                        },
                    )
                } else {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTabIndex,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        edgePadding = 12.dp,
                        divider = {},
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            )
                        },
                    ) {
                        visibleTabs.forEachIndexed { index, tab ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabId = tab.id },
                                text = { Text(tab.title) },
                            )
                        }
                        // Texte seul (pas icon+text) : même hauteur que les autres onglets (48dp).
                        Tab(
                            selected = false,
                            onClick = {
                                newTabTitle = ""
                                createDialogOpen = true
                            },
                            text = { Text("+") },
                        )
                    }
                }

                when (val tab = visibleTabs.getOrNull(selectedTabIndex)) {
                    null -> Unit
                    else -> when (tab.type) {
                        AideMemoireTabType.Markdown -> {
                            if (tab.isDefault) {
                                AideMemoireMarkdownTab(
                                    assetPath = tab.assetPath
                                        ?: defaultTabAssetPaths[tab.id]
                                        .orEmpty(),
                                    searchQuery = if (searchMode) searchQuery else null,
                                    requestedMatchIndex = if (searchMode) searchNavIndex else null,
                                    searchScrollNonce = if (searchMode) searchScrollNonce else 0,
                                    onMatchCountChanged = { searchMatchCount = it },
                                )
                            } else {
                                if (editMode) {
                                    AideMemoireMarkdownEditor(
                                        value = editValue,
                                        onValueChange = {
                                            editValue = it
                                            editBuffer = it.text
                                        },
                                    )
                                } else {
                                    AideMemoireMarkdownCustomTab(
                                        markdown = tab.markdown ?: "",
                                        searchQuery = if (searchMode) searchQuery else null,
                                        requestedMatchIndex = if (searchMode) searchNavIndex else null,
                                        searchScrollNonce = if (searchMode) searchScrollNonce else 0,
                                        onMatchCountChanged = { searchMatchCount = it },
                                    )
                                }
                            }
                        }
                        // Compat ancienne config: plus de viewer PDF interne.
                        AideMemoireTabType.Pdf -> AideMemoireMarkdownCustomTab(
                            markdown = tab.markdown ?: "Ouvrir le PDF : assetpdf://${tab.assetPath ?: ""}",
                            searchQuery = if (searchMode) searchQuery else null,
                            requestedMatchIndex = if (searchMode) searchNavIndex else null,
                            searchScrollNonce = if (searchMode) searchScrollNonce else 0,
                            onMatchCountChanged = { searchMatchCount = it },
                        )
                    }
                }
            }
        }
    }

    if (createDialogOpen) {
        CreateTabDialog(
            title = newTabTitle,
            onTitleChange = { newTabTitle = it },
            onDismiss = { createDialogOpen = false },
            onCreate = {
                val safeTitle = newTabTitle.trim().ifBlank { "Nouvel onglet" }
                scope.launch { repository.createBlankMarkdown(title = safeTitle) }
                createDialogOpen = false
            },
        )
    }

    // Recherche : barre dédiée entre TopAppBar et onglets (voir AideMemoireSearchBar).
}

private sealed interface AideMemoireTab {
    val title: String

    data class Markdown(
        override val title: String,
        val assetPath: String,
    ) : AideMemoireTab

    data class Pdf(
        override val title: String,
        val assetPath: String,
    ) : AideMemoireTab
}

@Composable
private fun AideMemoireMarkdownCustomTab(
    markdown: String,
    searchQuery: String? = null,
    requestedMatchIndex: Int? = null,
    searchScrollNonce: Int = 0,
    onMatchCountChanged: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    AideMemoireMarkdownCommon(
        context = context,
        uriHandler = uriHandler,
        raw = markdown,
        emptyText = "Onglet vide.",
        searchQuery = searchQuery,
        requestedMatchIndex = requestedMatchIndex,
        searchScrollNonce = searchScrollNonce,
        onMatchCountChanged = onMatchCountChanged,
    )
}

@Composable
private fun AideMemoireMarkdownTab(
    assetPath: String,
    searchQuery: String? = null,
    requestedMatchIndex: Int? = null,
    searchScrollNonce: Int = 0,
    onMatchCountChanged: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val raw = remember(assetPath) { readAssetText(context, assetPath) }
    AideMemoireMarkdownCommon(
        context = context,
        uriHandler = uriHandler,
        raw = raw,
        emptyText = "Aucun contenu.",
        searchQuery = searchQuery,
        requestedMatchIndex = requestedMatchIndex,
        searchScrollNonce = searchScrollNonce,
        onMatchCountChanged = onMatchCountChanged,
    )
}

@Composable
private fun AideMemoireMarkdownCommon(
    context: Context,
    uriHandler: androidx.compose.ui.platform.UriHandler,
    raw: String,
    emptyText: String,
    searchQuery: String?,
    requestedMatchIndex: Int?,
    searchScrollNonce: Int,
    onMatchCountChanged: (Int) -> Unit,
) {
    val lines = remember(raw) { raw.lines() }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val normalizedQuery = remember(searchQuery) { searchQuery?.trim().orEmpty() }
    val matchPositions = remember(lines, normalizedQuery) {
        if (normalizedQuery.isBlank()) emptyList()
        else {
            val q = normalizeSearchText(normalizedQuery)
            lines.flatMapIndexed { lineIndex, line ->
                val normalized = normalizeSearchTextWithMap(line)
                findAllOccurrences(normalized.normalized, q).mapNotNull { startIdx ->
                    val endIdx = startIdx + q.length
                    val startOriginal = normalized.mapToOriginal(startIdx) ?: return@mapNotNull null
                    val endOriginal = normalized.mapToOriginal(endIdx - 1)?.plus(1) ?: return@mapNotNull null
                    MatchPosition(lineIndex = lineIndex, start = startOriginal, end = endOriginal)
                }
            }
        }
    }

    LaunchedEffect(matchPositions.size) {
        onMatchCountChanged(matchPositions.size)
    }

    val clampedRequested = remember(requestedMatchIndex, matchPositions.size) {
        requestedMatchIndex?.coerceIn(0, (matchPositions.size - 1).coerceAtLeast(0))
    }

    LaunchedEffect(clampedRequested, searchScrollNonce) {
        val idx = clampedRequested ?: return@LaunchedEffect
        val mp = matchPositions.getOrNull(idx) ?: return@LaunchedEffect
        listState.animateScrollToItem(mp.lineIndex)
        delay(80)
        // Après scroll, vérifier que la ligne n'est pas masquée en bas du viewport.
        val layout = listState.layoutInfo
        val item = layout.visibleItemsInfo.firstOrNull { it.index == mp.lineIndex }
        if (item != null) {
            val bottom = item.offset + item.size
            val safeBottom = layout.viewportEndOffset - with(density) { 24.dp.roundToPx() }
            if (bottom > safeBottom) {
                listState.scrollBy((bottom - safeBottom).toFloat())
            }
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {
        if (raw.isBlank()) {
            item {
                Text(
                    text = emptyText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            return@LazyColumn
        }

        if (normalizedQuery.isNotBlank()) {
            item {
                if (matchPositions.isEmpty()) {
                    Text(
                        text = "Aucun résultat.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                } else {
                    Text(
                        text = "${matchPositions.size} résultat(s).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }

        items(lines.size) { idx ->
            val trimmed = lines[idx].trim()
            if (trimmed.isEmpty()) return@items

            // Image locale: ![](appimg://token)
            val imageToken = parseMarkdownImageToken(trimmed)
            if (imageToken != null) {
                AideMemoireLocalImage(token = imageToken)
                return@items
            }

            val lineHighlights = matchPositions.filter { it.lineIndex == idx }.map { it.start until it.end }

            when {
                trimmed.startsWith("### ") -> MarkdownLine(
                    text = trimmed.removePrefix("### "),
                    style = aideMemoireMarkdownH3TextStyle(),
                    highlights = lineHighlights,
                    modifier = Modifier.padding(top = 6.dp),
                )
                trimmed.startsWith("## ") -> MarkdownLine(
                    text = trimmed.removePrefix("## "),
                    style = MaterialTheme.typography.titleLarge,
                    highlights = lineHighlights,
                    modifier = Modifier.padding(top = 8.dp),
                )
                trimmed.startsWith("# ") -> MarkdownLine(
                    text = trimmed.removePrefix("# "),
                    style = MaterialTheme.typography.headlineSmall,
                    highlights = lineHighlights,
                )
                trimmed.startsWith("- ") -> {
                    val body = trimmed.removePrefix("- ")
                    MarkdownBodyLineWithLinks(
                        text = "• $body",
                        onOpenUrl = { openAideMemoireLink(context, uriHandler, it) },
                        highlights = lineHighlights,
                        bulletCharStyle = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = MaterialTheme.colorScheme.primary,
                        ),
                    )
                }
                else -> MarkdownBodyLineWithLinks(
                    text = trimmed,
                    onOpenUrl = { openAideMemoireLink(context, uriHandler, it) },
                    highlights = lineHighlights,
                )
            }
        }
    }
}

@Composable
private fun AideMemoireLocalImage(token: String) {
    val context = LocalContext.current
    val image = remember(token) { loadLocalImageBitmap(context, token) }
    if (image == null) {
        Text(
            text = "Image introuvable.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    Image(
        bitmap = image,
        contentDescription = "Image",
        modifier = Modifier
            .fillMaxWidth(),
    )
}

private fun parseMarkdownImageToken(line: String): String? {
    // Support minimal: ![](appimg://token)
    val regex = Regex("""!\[[^\]]*]\((appimg://[^)]+)\)""")
    val match = regex.find(line) ?: return null
    val uri = match.groupValues.getOrNull(1) ?: return null
    return uri.removePrefix("appimg://").trim()
}

private fun loadLocalImageBitmap(context: Context, token: String): ImageBitmap? {
    val file = File(File(context.filesDir, "aide_memoire_images"), token)
    if (!file.exists()) return null
    val bmp = BitmapFactory.decodeFile(file.absolutePath) ?: return null
    return bmp.asImageBitmap()
}

@Composable
private fun AideMemoireMarkdownEditor(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
) {
    val h1 = MaterialTheme.typography.headlineSmall
    val h2 = MaterialTheme.typography.titleLarge
    val h3 = aideMemoireMarkdownH3TextStyle()
    val bulletMarkerStyle = SpanStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.primary,
    )
    val transformation = remember(h1, h2, h3, bulletMarkerStyle) {
        AideMemoireWysiwygMarkdownTransformation(
            h1Style = SpanStyle(fontSize = h1.fontSize, fontWeight = FontWeight.SemiBold),
            h2Style = SpanStyle(fontSize = h2.fontSize, fontWeight = FontWeight.SemiBold),
            h3Style = SpanStyle(fontSize = h3.fontSize, fontWeight = FontWeight.SemiBold),
            bulletMarkerStyle = bulletMarkerStyle,
        )
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            label = { Text("Contenu") },
            visualTransformation = transformation,
        )
    }
}

// Recherche : barre dédiée entre TopAppBar et onglets.

private fun openAideMemoireLink(context: Context, uriHandler: androidx.compose.ui.platform.UriHandler, url: String) {
    if (url.startsWith("assetpdf://")) {
        val assetPath = url.removePrefix("assetpdf://").trimStart('/')
        openPdfAssetExternally(context, assetPath)
        return
    }
    uriHandler.openUri(url)
}

private fun openPdfAssetExternally(context: Context, assetPath: String) {
    val file = ensureAssetCopiedToCache(context, assetPath)
    val uri = androidx.core.content.FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file,
    )
    val intent = Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, "application/pdf")
        .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

    try {
        context.startActivity(Intent.createChooser(intent, "Ouvrir le PDF"))
    } catch (_: ActivityNotFoundException) {
        // Pas de viewer PDF installé -> ne pas crasher.
    }
}

private fun normalizeSearchText(input: String): String {
    val normalized = Normalizer.normalize(input, Normalizer.Form.NFD)
    return normalized.replace(Regex("\\p{Mn}+"), "").lowercase()
}

private fun copyImageIntoApp(context: Context, uri: Uri): String {
    val dir = File(context.filesDir, "aide_memoire_images")
    if (!dir.exists()) dir.mkdirs()
    val token = "${UUID.randomUUID()}.img"
    val outFile = File(dir, token)
    context.contentResolver.openInputStream(uri).use { input ->
        if (input == null) throw IllegalStateException("Impossible de lire l'image.")
        FileOutputStream(outFile).use { output -> input.copyTo(output) }
    }
    return token
}

@Composable
private fun aideMemoireMarkdownH3TextStyle(): androidx.compose.ui.text.TextStyle =
    MaterialTheme.typography.titleMedium.copy(
        fontSize = (MaterialTheme.typography.bodyLarge.fontSize.value + 2f).sp,
    )

@Composable
private fun aideMemoireMarkdownBodyTextStyle(): androidx.compose.ui.text.TextStyle {
    val body = MaterialTheme.typography.bodyLarge
    return body.copy(
        lineHeight = (body.fontSize.value * 1.25f).sp,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun MarkdownLine(
    text: String,
    style: androidx.compose.ui.text.TextStyle,
    highlights: List<IntRange> = emptyList(),
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = buildMarkdownInlineAnnotated(text, highlights),
        style = style.copy(fontWeight = FontWeight.SemiBold),
        color = MaterialTheme.colorScheme.onSurface,
    )
}

private val markdownUrlRegex = Regex("(https?://\\S+)")

private fun buildInlineMarkdownAnnotated(
    text: String,
    boldStyle: SpanStyle,
    italicStyle: SpanStyle,
    linkStyle: SpanStyle,
): AnnotatedString {
    return buildAnnotatedString {
        var i = 0
        while (i < text.length) {
            val urlMatch = markdownUrlRegex.find(text, i)
            if (urlMatch != null && urlMatch.range.first == i) {
                val raw = text.substring(urlMatch.range)
                val url = raw.trimEnd('.', ',', ')', ']')
                pushStringAnnotation(tag = "URL", annotation = url)
                withStyle(linkStyle) { append(url) }
                pop()
                i = urlMatch.range.last + 1
                continue
            }

            if (text.startsWith("**", i)) {
                val close = text.indexOf("**", startIndex = i + 2)
                if (close > i + 2) {
                    withStyle(boldStyle) { append(text.substring(i + 2, close)) }
                    i = close + 2
                    continue
                }
            }

            if (text[i] == '_') {
                val close = text.indexOf('_', startIndex = i + 1)
                if (close > i + 1) {
                    val prevOk = i == 0 || text[i - 1].isWhitespace() || text[i - 1] == '\n'
                    val nextOk = close + 1 >= text.length || text[close + 1].isWhitespace() || text[close + 1] == '\n'
                    if (prevOk && nextOk) {
                        withStyle(italicStyle) { append(text.substring(i + 1, close)) }
                        i = close + 1
                        continue
                    }
                }
            }

            append(text[i])
            i++
        }
    }
}

@Composable
private fun buildMarkdownInlineAnnotated(
    text: String,
    highlights: List<IntRange>,
    bulletCharStyle: SpanStyle? = null,
): AnnotatedString {
    val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)
    val italicStyle = SpanStyle(fontStyle = FontStyle.Italic)
    val linkStyle = SpanStyle(
        color = MaterialTheme.colorScheme.primary,
        textDecoration = TextDecoration.Underline,
        fontWeight = FontWeight.Medium,
    )
    val base = buildInlineMarkdownAnnotated(text, boldStyle, italicStyle, linkStyle)

    val withBullet = if (bulletCharStyle != null && text.startsWith("•")) {
        buildAnnotatedString {
            append(base.text)
            base.spanStyles.forEach { addStyle(it.item, it.start, it.end) }
            base.getStringAnnotations(0, base.text.length).forEach { ann ->
                addStringAnnotation(tag = ann.tag, annotation = ann.item, start = ann.start, end = ann.end)
            }
            addStyle(bulletCharStyle, 0, 1)
        }
    } else {
        base
    }

    if (highlights.isEmpty()) return withBullet

    val highlightStyle = SpanStyle(
        background = MaterialTheme.colorScheme.tertiaryContainer,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
    )
    return buildAnnotatedString {
        append(withBullet.text)
        withBullet.spanStyles.forEach { addStyle(it.item, it.start, it.end) }
        withBullet.getStringAnnotations(0, withBullet.text.length).forEach { ann ->
            addStringAnnotation(tag = ann.tag, annotation = ann.item, start = ann.start, end = ann.end)
        }
        highlights.forEach { range ->
            val start = range.first.coerceIn(0, withBullet.text.length)
            val end = (range.last + 1).coerceIn(0, withBullet.text.length)
            if (end > start) addStyle(highlightStyle, start, end)
        }
    }
}

@Composable
private fun MarkdownBodyLineWithLinks(
    text: String,
    onOpenUrl: (String) -> Unit,
    highlights: List<IntRange> = emptyList(),
    bulletCharStyle: SpanStyle? = null,
) {
    val annotated = buildMarkdownInlineAnnotated(text, highlights, bulletCharStyle)
    ClickableTextLine(
        text = annotated,
        onOpenUrl = onOpenUrl,
    )
}

@Composable
private fun ClickableTextLine(
    text: AnnotatedString,
    onOpenUrl: (String) -> Unit,
) {
    androidx.compose.foundation.text.ClickableText(
        text = text,
        style = aideMemoireMarkdownBodyTextStyle(),
        onClick = { offset ->
            val url = text.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()
                ?.item
            if (!url.isNullOrBlank()) onOpenUrl(url)
        },
    )
}

// PDF viewer interne supprimé: les PDFs s'ouvrent via app externe.

private fun readAssetText(context: Context, assetPath: String): String =
    runCatching {
        context.assets.open(assetPath).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }.getOrElse { "" }

private fun ensureAssetCopiedToCache(context: Context, assetPath: String): File {
    val name = assetPath.substringAfterLast('/').ifBlank { "document.pdf" }
    val outFile = File(context.cacheDir, name)
    if (outFile.exists() && outFile.length() > 0) return outFile
    context.assets.open(assetPath).use { input ->
        FileOutputStream(outFile).use { output ->
            input.copyTo(output)
        }
    }
    return outFile
}

// --- Lot 2: personnalisation des onglets (persistée) ---

private val Context.aideMemoireDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "aide_memoire_preferences",
)

private val defaultTabAssetPaths = mapOf(
    "default_memo_pse" to "aide_memoire/md/memo_pse.md",
    "default_recos_pdf" to "aide_memoire/md/recos_officielles.md",
)

@Serializable
private enum class AideMemoireTabType { Markdown, Pdf }

@Serializable
private data class AideMemoireTabConfig(
    val id: String,
    val title: String,
    val type: AideMemoireTabType,
    val isDefault: Boolean,
    val visible: Boolean,
    val assetPath: String? = null,
    val markdown: String? = null,
)

private class AideMemoireTabsRepository(
    private val context: Context,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val tabsKey = stringPreferencesKey("tabs_json_v1")

    val tabs: Flow<List<AideMemoireTabConfig>> =
        context.aideMemoireDataStore.data.map { prefs ->
            val raw = prefs[tabsKey]
            val parsed = raw?.let { runCatching { json.decodeFromString<List<AideMemoireTabConfig>>(it) }.getOrNull() }
            val initial = parsed?.takeIf { it.isNotEmpty() } ?: defaultTabs()
            normalizeAndFixVisibility(
                migrateRemoveDeprecatedTabs(repairDefaultTabs(migrateAwayFromPdfTabs(initial))),
            )
        }

    private fun migrateRemoveDeprecatedTabs(input: List<AideMemoireTabConfig>): List<AideMemoireTabConfig> =
        input.filter { it.id != "default_sites" }

    /** Rétablit le chemin asset des onglets d'usine (ex. après migration PDF → markdown). */
    private fun repairDefaultTabs(input: List<AideMemoireTabConfig>): List<AideMemoireTabConfig> {
        val canonical = defaultTabs().associateBy { it.id }
        return input.map { tab ->
            val ref = canonical[tab.id] ?: return@map tab
            if (!ref.isDefault) return@map tab
            tab.copy(
                type = AideMemoireTabType.Markdown,
                assetPath = ref.assetPath,
                markdown = null,
            )
        }
    }

    private fun migrateAwayFromPdfTabs(input: List<AideMemoireTabConfig>): List<AideMemoireTabConfig> =
        input.map { tab ->
            if (tab.type != AideMemoireTabType.Pdf) return@map tab

            val asset = tab.assetPath ?: "aide_memoire/pdf/2024_pse.pdf"
            val md = tab.markdown ?: buildString {
                append("# ${tab.title}\n\n")
                append("- Ouvrir le PDF : assetpdf://")
                append(asset)
                append("\n")
            }

            tab.copy(
                type = AideMemoireTabType.Markdown,
                assetPath = null,
                markdown = md,
            )
        }

    suspend fun createBlankMarkdown(title: String) {
        updateTabs { current ->
            val next = current.toMutableList()
            next.add(
                AideMemoireTabConfig(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    type = AideMemoireTabType.Markdown,
                    isDefault = false,
                    visible = true,
                    markdown = "",
                ),
            )
            next
        }
    }

    suspend fun duplicate(id: String) {
        updateTabs { current ->
            val source = current.firstOrNull { it.id == id } ?: return@updateTabs current
            val next = current.toMutableList()
            val title = if (source.title.endsWith(" (copie)")) source.title else "${source.title} (copie)"
            next.add(
                source.copy(
                    id = UUID.randomUUID().toString(),
                    title = title,
                    isDefault = false,
                    visible = true,
                ),
            )
            next
        }
    }

    suspend fun updateMarkdown(id: String, markdown: String) {
        updateTabs { current ->
            val idx = current.indexOfFirst { it.id == id }
            if (idx < 0) return@updateTabs current
            val tab = current[idx]
            if (tab.isDefault) return@updateTabs current
            current.toMutableList().apply {
                this[idx] = tab.copy(
                    type = AideMemoireTabType.Markdown,
                    markdown = markdown,
                )
            }
        }
    }

    suspend fun deleteCustom(id: String) {
        updateTabs { current ->
            val target = current.firstOrNull { it.id == id } ?: return@updateTabs current
            if (target.isDefault) return@updateTabs current

            val next = current.filterNot { it.id == id }
            normalizeAndFixVisibility(next)
        }
    }

    suspend fun toggleVisible(id: String) {
        updateTabs { current ->
            val idx = current.indexOfFirst { it.id == id }
            if (idx < 0) return@updateTabs current
            val tab = current[idx]

            // Garde-fou: au moins 1 onglet visible.
            val visibleCount = current.count { it.visible }
            val nextVisible = !tab.visible
            if (!nextVisible && visibleCount <= 1) return@updateTabs current

            current.toMutableList().apply {
                this[idx] = tab.copy(visible = nextVisible)
            }
        }
    }

    suspend fun move(id: String, delta: Int) {
        updateTabs { current ->
            val idx = current.indexOfFirst { it.id == id }
            if (idx < 0) return@updateTabs current
            val newIdx = (idx + delta).coerceIn(0, current.lastIndex)
            if (newIdx == idx) return@updateTabs current
            val next = current.toMutableList()
            val item = next.removeAt(idx)
            next.add(newIdx, item)
            next
        }
    }

    suspend fun moveToIndex(fromIndex: Int, toIndex: Int) {
        updateTabs { current ->
            if (current.isEmpty()) return@updateTabs current
            val from = fromIndex.coerceIn(0, current.lastIndex)
            val to = toIndex.coerceIn(0, current.lastIndex)
            if (from == to) return@updateTabs current
            val next = current.toMutableList()
            val item = next.removeAt(from)
            next.add(to, item)
            next
        }
    }

    private suspend fun updateTabs(transform: (List<AideMemoireTabConfig>) -> List<AideMemoireTabConfig>) {
        context.aideMemoireDataStore.edit { prefs ->
            val current = prefs[tabsKey]
                ?.let { runCatching { json.decodeFromString<List<AideMemoireTabConfig>>(it) }.getOrNull() }
                ?: defaultTabs()
            val updated = normalizeAndFixVisibility(transform(current))
            prefs[tabsKey] = json.encodeToString(updated)
        }
    }

    private fun defaultTabs(): List<AideMemoireTabConfig> =
        defaultTabAssetPaths.map { (id, assetPath) ->
            AideMemoireTabConfig(
                id = id,
                title = when (id) {
                    "default_memo_pse" -> "Mémo PSE"
                    "default_recos_pdf" -> "Recos. officielles"
                    else -> id
                },
                type = AideMemoireTabType.Markdown,
                isDefault = true,
                visible = true,
                assetPath = assetPath,
            )
        }

    private fun normalizeAndFixVisibility(input: List<AideMemoireTabConfig>): List<AideMemoireTabConfig> {
        val list = input.toMutableList()
        // Garde-fou: au moins 1 visible (si tout est masqué, on ré-affiche le premier).
        if (list.none { it.visible } && list.isNotEmpty()) {
            list[0] = list[0].copy(visible = true)
        }
        return list
    }
}

@Composable
private fun ManageTabsDialog(
    tabs: List<AideMemoireTabConfig>,
    onDismiss: () -> Unit,
    onMoveUp: (String) -> Unit,
    onMoveDown: (String) -> Unit,
    onToggleVisible: (String) -> Unit,
    onDuplicate: (String) -> Unit,
    onDelete: (String) -> Unit,
    onCreateNew: () -> Unit,
) {
    val visibleCount = remember(tabs) { tabs.count { it.visible } }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Onglets") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Créer, dupliquer, masquer, supprimer et réordonner.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(tabs, key = { it.id }) { tab ->
                        ManageTabRow(
                            tab = tab,
                            canHide = !(tab.visible && visibleCount <= 1),
                            onMoveUp = { onMoveUp(tab.id) },
                            onMoveDown = { onMoveDown(tab.id) },
                            onToggleVisible = { onToggleVisible(tab.id) },
                            onDuplicate = { onDuplicate(tab.id) },
                            onDelete = { onDelete(tab.id) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fermer") }
        },
        dismissButton = {
            TextButton(onClick = onCreateNew) { Text("Nouvel onglet") }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ManageTabsScreen(
    tabs: List<AideMemoireTabConfig>,
    onBack: () -> Unit,
    onMove: (fromIndex: Int, toIndex: Int) -> Unit,
    onToggleVisible: (String) -> Unit,
    onDuplicate: (String) -> Unit,
    onDelete: (String) -> Unit,
    onCreateNew: () -> Unit,
) {
    // Local list for smooth drag feedback.
    val localTabs = remember(tabs) { tabs.toMutableList() }
    val visibleCount = remember(localTabs) { localTabs.count { it.visible } }

    var draggingIndex by remember { mutableIntStateOf(-1) }
    var dragAccumulated by remember { mutableStateOf(0f) }
    val rowHeight: Dp = 72.dp
    val rowHeightPx = with(LocalDensity.current) { rowHeight.toPx() }.coerceAtLeast(1f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gérer les onglets") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = onCreateNew) {
                        Icon(Icons.Outlined.Add, contentDescription = "Nouvel onglet")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(localTabs, key = { it.id }) { tab ->
                val index = localTabs.indexOfFirst { it.id == tab.id }
                ManageTabRowDrag(
                    tab = tab,
                    canHide = !(tab.visible && visibleCount <= 1),
                    dragging = index == draggingIndex,
                    onStartDrag = {
                        draggingIndex = index
                        dragAccumulated = 0f
                    },
                    onDragDelta = { dy ->
                        if (draggingIndex < 0) return@ManageTabRowDrag
                        dragAccumulated += dy
                        // Permet de traverser plusieurs lignes d’un coup: on swap autant de fois que nécessaire.
                        while (dragAccumulated > rowHeightPx && draggingIndex < localTabs.lastIndex) {
                            localTabs.swap(draggingIndex, draggingIndex + 1)
                            draggingIndex += 1
                            dragAccumulated -= rowHeightPx
                        }
                        while (dragAccumulated < -rowHeightPx && draggingIndex > 0) {
                            localTabs.swap(draggingIndex, draggingIndex - 1)
                            draggingIndex -= 1
                            dragAccumulated += rowHeightPx
                        }
                    },
                    onEndDrag = {
                        // Commit new order to persistence.
                        if (draggingIndex >= 0) {
                            // We only know final list order; easiest is to apply as a sequence of moves
                            // from the original "tabs" list order to the local order.
                            val original = tabs.map { it.id }
                            val updated = localTabs.map { it.id }
                            if (original != updated) {
                                // Apply by rebuilding through moveToIndex on repository side:
                                // we call onMove repeatedly based on current positions.
                                val working = original.toMutableList()
                                updated.forEachIndexed { targetIndex, id ->
                                    val currentIndex = working.indexOf(id)
                                    if (currentIndex != targetIndex && currentIndex >= 0) {
                                        onMove(currentIndex, targetIndex)
                                        val item = working.removeAt(currentIndex)
                                        working.add(targetIndex, item)
                                    }
                                }
                            }
                        }
                        draggingIndex = -1
                        dragAccumulated = 0f
                    },
                    onToggleVisible = { onToggleVisible(tab.id) },
                    onDuplicate = { onDuplicate(tab.id) },
                    onDelete = { onDelete(tab.id) },
                )
            }
        }
    }
}

private fun <T> MutableList<T>.swap(i: Int, j: Int) {
    if (i == j) return
    val tmp = this[i]
    this[i] = this[j]
    this[j] = tmp
}

@Composable
private fun ManageTabRowDrag(
    tab: AideMemoireTabConfig,
    canHide: Boolean,
    dragging: Boolean,
    onStartDrag: () -> Unit,
    onDragDelta: (Float) -> Unit,
    onEndDrag: () -> Unit,
    onToggleVisible: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .pointerInput(tab.id) {
                // Drag on the whole row (simple v1).
                detectDragGestures(
                    onDragStart = { onStartDrag() },
                    onDragEnd = { onEndDrag() },
                    onDragCancel = { onEndDrag() },
                    onDrag = { change, dragAmount ->
                        change.consumeAllChanges()
                        onDragDelta(dragAmount.y)
                    },
                )
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.DragHandle,
                contentDescription = "Réordonner",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(end = 6.dp),
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = when (tab.type) {
                        AideMemoireTabType.Markdown -> if (tab.isDefault) "Markdown (défaut)" else "Markdown (perso)"
                        AideMemoireTabType.Pdf -> "Markdown"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(
                onClick = { if (canHide) onToggleVisible() },
                enabled = canHide,
            ) {
                Icon(
                    if (tab.visible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = if (tab.visible) "Masquer" else "Afficher",
                    tint = if (tab.visible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(onClick = onDuplicate) {
                Icon(
                    Icons.Outlined.ContentCopy,
                    contentDescription = "Dupliquer",
                    tint = MaterialTheme.colorScheme.tertiary,
                )
            }

            if (!tab.isDefault) {
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = "Supprimer",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            } else {
                Spacer(Modifier.padding(24.dp))
            }
        }
    }
}

@Composable
private fun ManageTabRow(
    tab: AideMemoireTabConfig,
    canHide: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleVisible: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tab.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = when (tab.type) {
                        AideMemoireTabType.Markdown -> if (tab.isDefault) "Markdown (défaut)" else "Markdown (perso)"
                        AideMemoireTabType.Pdf -> "Markdown"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            IconButton(onClick = onMoveUp) {
                Icon(Icons.Outlined.KeyboardArrowUp, contentDescription = "Monter")
            }
            IconButton(onClick = onMoveDown) {
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = "Descendre")
            }

            IconButton(
                onClick = { if (canHide) onToggleVisible() },
                enabled = canHide,
            ) {
                Icon(
                    if (tab.visible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                    contentDescription = if (tab.visible) "Masquer" else "Afficher",
                )
            }

            IconButton(onClick = onDuplicate) {
                Icon(Icons.Outlined.ContentCopy, contentDescription = "Dupliquer")
            }

            if (!tab.isDefault) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Outlined.Delete, contentDescription = "Supprimer")
                }
            } else {
                Spacer(Modifier.padding(24.dp))
            }
        }
    }
}

@Composable
private fun CreateTabDialog(
    title: String,
    onTitleChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onCreate: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouvel onglet") },
        text = {
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChange,
                label = { Text("Titre") },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = onCreate) { Text("Créer") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        },
    )
}
