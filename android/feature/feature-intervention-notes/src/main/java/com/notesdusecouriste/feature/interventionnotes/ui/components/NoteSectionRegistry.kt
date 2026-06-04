package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun LazyListScope.noteSectionHeader(
    key: String,
    title: String,
    kind: NoteSectionKind,
    registry: NoteStickyHeaderRegistry,
) {
    registry.registerBlock(key = key, title = title, kind = kind)
    item(key = key) {
        StickySectionHeader(
            title = title,
            kind = kind,
            modifier = Modifier
                .headerHiddenWhenPinned(key)
                .padding(bottom = 0.dp),
        )
    }
    registry.advance()
}

fun LazyListScope.noteSectionItem(
    key: String,
    registry: NoteStickyHeaderRegistry,
    content: @Composable LazyItemScope.() -> Unit,
) {
    item(key = key, content = content)
    registry.advance()
}

fun LazyListScope.noteMesuresBlockHeader(
    key: String,
    title: String,
    registry: NoteStickyHeaderRegistry,
    header: (@Composable () -> Unit)? = null,
    tabs: @Composable () -> Unit,
) {
    registry.registerMesuresBlock(key = key, title = title, tabs = tabs)
    item(key = key) {
        Column(
            Modifier
                .headerHiddenWhenPinned(key)
                .onSizeChanged { registry.updateParentBlockHeightPx(it.height) },
        ) {
            if (header != null) {
                header()
            } else {
                StickySectionHeader(title = title, kind = NoteSectionKind.Mesures)
            }
            tabs()
        }
    }
    registry.advance()
}

fun LazyListScope.noteMesureSubSectionHeader(
    key: String,
    title: String,
    kind: NoteSectionKind,
    registry: NoteStickyHeaderRegistry,
    expanded: Boolean = true,
    onToggleExpanded: (() -> Unit)? = null,
) {
    registry.registerSubsection(key = key, title = title, kind = kind)
    item(key = key) {
        val headerModifier = Modifier
            .headerHiddenWhenPinned(key)
            .padding(bottom = 0.dp)
            .then(
                if (onToggleExpanded != null) {
                    Modifier.clickable(onClick = onToggleExpanded)
                } else {
                    Modifier
                },
            )
        StickySectionHeader(
            title = title,
            kind = kind,
            isSubSection = true,
            modifier = headerModifier,
            indentStart = 12.dp,
            trailing = if (onToggleExpanded != null) {
                {
                    Icon(
                        imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                null
            },
        )
    }
    registry.advance()
}
