package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListLayoutInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

internal data class PinnedHeaderRender(
    val slot: NoteStickyHeaderSlot,
    val pushOffPx: Int,
)

/**
 * Position Y (px) du bas du bandeau bloc parent (ex. Mesures + onglets).
 * Quand le bloc est épinglé dans l'overlay, on utilise sa hauteur mesurée — pas la position
 * de l'item dans la liste (souvent décalée / masquée), qui provoquait un sticky trop tôt.
 */
internal fun stickyStackBottomPx(
    layoutInfo: LazyListLayoutInfo,
    activeBlock: NoteStickyHeaderSlot,
    parentBlockHeightPx: Int,
    listItemSpacingPx: Int,
): Int {
    val top = layoutInfo.beforeContentPadding
    val blockItem = layoutInfo.visibleItemsInfo.find { it.index == activeBlock.itemIndex }
    val blockPinnedInOverlay = isBlockPinned(activeBlock, layoutInfo) &&
        (blockItem == null || blockItem.offset <= top)

    return when {
        blockPinnedInOverlay && parentBlockHeightPx > 0 -> top + parentBlockHeightPx
        blockItem != null -> blockItem.offset + blockItem.size + listItemSpacingPx
        parentBlockHeightPx > 0 -> top + parentBlockHeightPx
        else -> top
    }
}

internal fun isBlockPinned(
    slot: NoteStickyHeaderSlot,
    layoutInfo: LazyListLayoutInfo,
): Boolean {
    val top = layoutInfo.beforeContentPadding
    val firstVisibleIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: return false
    if (slot.itemIndex < firstVisibleIndex) return true
    val item = layoutInfo.visibleItemsInfo.find { it.index == slot.itemIndex } ?: return false
    return item.offset <= top
}

internal fun isSubsectionPinned(
    sub: NoteStickyHeaderSlot,
    activeBlock: NoteStickyHeaderSlot,
    layoutInfo: LazyListLayoutInfo,
    parentBlockHeightPx: Int,
    listItemSpacingPx: Int,
): Boolean {
    if (!isBlockPinned(activeBlock, layoutInfo)) return false

    val stackBottom = stickyStackBottomPx(
        layoutInfo = layoutInfo,
        activeBlock = activeBlock,
        parentBlockHeightPx = parentBlockHeightPx,
        listItemSpacingPx = listItemSpacingPx,
    )

    val subItem = layoutInfo.visibleItemsInfo.find { it.index == sub.itemIndex }
    if (subItem != null) {
        return subItem.offset <= stackBottom
    }

    val firstVisibleIndex = layoutInfo.visibleItemsInfo.firstOrNull()?.index ?: return false
    return sub.itemIndex < firstVisibleIndex
}

internal fun computePinnedHeaderStack(
    layoutInfo: LazyListLayoutInfo,
    slots: List<NoteStickyHeaderSlot>,
    parentBlockHeightPx: Int,
    listItemSpacingPx: Int,
): List<PinnedHeaderRender> {
    if (slots.isEmpty()) return emptyList()

    val top = layoutInfo.beforeContentPadding
    val visible = layoutInfo.visibleItemsInfo

    val pinnedBlocks = slots.filter { it.level == NoteHeaderLevel.Block && isBlockPinned(it, layoutInfo) }
    if (pinnedBlocks.isEmpty()) return emptyList()

    val activeBlock = pinnedBlocks.maxByOrNull { it.itemIndex } ?: return emptyList()

    val activeSub = slots
        .filter { it.level == NoteHeaderLevel.Subsection && it.itemIndex > activeBlock.itemIndex }
        .filter {
            isSubsectionPinned(
                sub = it,
                activeBlock = activeBlock,
                layoutInfo = layoutInfo,
                parentBlockHeightPx = parentBlockHeightPx,
                listItemSpacingPx = listItemSpacingPx,
            )
        }
        .maxByOrNull { it.itemIndex }

    val stack = buildList {
        add(activeBlock)
        if (activeSub != null) add(activeSub)
    }

    return stack.mapIndexed { index, slot ->
        val itemInfo = visible.find { it.index == slot.itemIndex }
        val pushOffPx = if (itemInfo != null) {
            val nextInfo = when (slot.level) {
                // Option B : le sous-titre ne doit jamais « pousser » le bloc Mesures vers le haut.
                // Le bloc reste accroché, et seuls les sous-titres se remplacent sous le bandeau.
                NoteHeaderLevel.Block ->
                    slots
                        .firstOrNull { it.level == NoteHeaderLevel.Block && it.itemIndex > slot.itemIndex }
                        ?.let { next -> visible.find { it.index == next.itemIndex } }

                NoteHeaderLevel.Subsection -> {
                    val nextInStack = stack.getOrNull(index + 1)
                    when {
                        nextInStack != null ->
                            visible.find { it.index == nextInStack.itemIndex }
                        else ->
                            slots
                                .firstOrNull { it.itemIndex > slot.itemIndex && !stack.contains(it) }
                                ?.let { next -> visible.find { it.index == next.itemIndex } }
                    }
                }
            }
            if (nextInfo != null) {
                val overlap = itemInfo.size + itemInfo.offset - nextInfo.offset
                if (overlap > 0) -overlap else 0
            } else {
                0
            }
        } else {
            0
        }
        PinnedHeaderRender(slot = slot, pushOffPx = pushOffPx)
    }
}

@Composable
fun StackedStickyHeadersOverlay(
    listState: LazyListState,
    registry: NoteStickyHeaderRegistry,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val listItemSpacingPx = with(LocalDensity.current) { 8.dp.roundToPx() }

    val pinnedStack by remember(listState, registry.slots.size, registry.parentBlockHeightPx) {
        derivedStateOf {
            computePinnedHeaderStack(
                layoutInfo = listState.layoutInfo,
                slots = registry.slots,
                parentBlockHeightPx = registry.parentBlockHeightPx,
                listItemSpacingPx = listItemSpacingPx,
            )
        }
    }

    if (pinnedStack.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = contentPadding.calculateLeftPadding(LayoutDirection.Ltr),
                end = contentPadding.calculateRightPadding(LayoutDirection.Ltr),
                top = contentPadding.calculateTopPadding(),
            )
            // Laisse passer les touches vers les onglets interactifs dans la liste.
            .pointerInteropFilter { false }
            .zIndex(8f),
    ) {
        pinnedStack.forEach { pinned ->
            val isParentBlock = pinned.slot.level == NoteHeaderLevel.Block
            Box(
                modifier = Modifier
                    .then(
                        if (isParentBlock) {
                            Modifier.onSizeChanged { registry.updateParentBlockHeightPx(it.height) }
                        } else {
                            Modifier
                        },
                    )
                    .offset { IntOffset(0, pinned.pushOffPx) },
            ) {
                NoteStickyHeaderContent(
                    slot = pinned.slot,
                    tabs = if (pinned.slot.kind == NoteSectionKind.Mesures) registry.mesuresTabs else null,
                )
            }
        }
    }
}

@Composable
internal fun NoteStickyHeaderContent(
    slot: NoteStickyHeaderSlot,
    tabs: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier,
) {
    if (tabs != null) {
        Column(modifier = modifier) {
            if (slot.kind == NoteSectionKind.Mesures) {
                StickySectionHeader(
                    title = slot.title,
                    kind = slot.kind,
                    trailing = { MesuresClockText() },
                )
            } else {
                StickySectionHeader(
                    title = slot.title,
                    kind = slot.kind,
                )
            }
            tabs()
        }
    } else {
        StickySectionHeader(
            title = slot.title,
            kind = slot.kind,
            isSubSection = slot.level == NoteHeaderLevel.Subsection,
            modifier = modifier.padding(bottom = 4.dp),
        )
    }
}

@Composable
internal fun Modifier.headerHiddenWhenPinned(key: String): Modifier {
    val pinnedKeys = LocalPinnedHeaderKeys.current
    return alpha(if (key in pinnedKeys) 0f else 1f)
}
