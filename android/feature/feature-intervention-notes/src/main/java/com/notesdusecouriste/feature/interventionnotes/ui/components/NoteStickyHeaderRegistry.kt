package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf

enum class NoteHeaderLevel {
    Block,
    Subsection,
}

data class NoteStickyHeaderSlot(
    val itemIndex: Int,
    val key: String,
    val title: String,
    val kind: NoteSectionKind,
    val level: NoteHeaderLevel,
)

/** Clés des en-têtes actuellement épinglés dans l'overlay (masque le doublon dans la liste). */
val LocalPinnedHeaderKeys = staticCompositionLocalOf<Set<String>> { emptySet() }

class NoteStickyHeaderRegistry {
    var itemIndex: Int = 0
        private set

    val slots = mutableListOf<NoteStickyHeaderSlot>()

    /** Onglets du bloc Mesures, rendus dans la liste et dans l'overlay. */
    var mesuresTabs: (@Composable () -> Unit)? = null
        private set

    /** Hauteur Mesures + onglets (px), pour caler le sticky des sous-titres sous ce bandeau. */
    var parentBlockHeightPx: Int = 0

    fun reset() {
        itemIndex = 0
        slots.clear()
        mesuresTabs = null
        parentBlockHeightPx = 0
    }

    fun updateParentBlockHeightPx(heightPx: Int) {
        if (heightPx > parentBlockHeightPx) {
            parentBlockHeightPx = heightPx
        }
    }

    fun advance() {
        itemIndex++
    }

    fun registerBlock(
        key: String,
        title: String,
        kind: NoteSectionKind,
    ) {
        slots.add(
            NoteStickyHeaderSlot(
                itemIndex = itemIndex,
                key = key,
                title = title,
                kind = kind,
                level = NoteHeaderLevel.Block,
            ),
        )
    }

    fun registerMesuresBlock(
        key: String,
        title: String,
        tabs: @Composable () -> Unit,
    ) {
        registerBlock(key = key, title = title, kind = NoteSectionKind.Mesures)
        mesuresTabs = tabs
    }

    fun registerSubsection(
        key: String,
        title: String,
        kind: NoteSectionKind,
    ) {
        slots.add(
            NoteStickyHeaderSlot(
                itemIndex = itemIndex,
                key = key,
                title = title,
                kind = kind,
                level = NoteHeaderLevel.Subsection,
            ),
        )
    }
}
