package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StickySectionHeader(
    title: String,
    kind: NoteSectionKind,
    modifier: Modifier = Modifier,
    isSubSection: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    indentStart: Dp = 0.dp,
) {
    StickySectionHeader(
        title = AnnotatedString(title),
        kind = kind,
        modifier = modifier,
        isSubSection = isSubSection,
        trailing = trailing,
        indentStart = indentStart,
    )
}

@Composable
fun StickySectionHeader(
    title: AnnotatedString,
    kind: NoteSectionKind,
    modifier: Modifier = Modifier,
    isSubSection: Boolean = false,
    trailing: (@Composable () -> Unit)? = null,
    indentStart: Dp = 0.dp,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = if (isSubSection) {
            MaterialTheme.colorScheme.surfaceContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        tonalElevation = if (isSubSection) 0.dp else 2.dp,
    ) {
        val baseHorizontal = 16.dp
        val horizontalStart = baseHorizontal + (if (isSubSection) indentStart else 0.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = horizontalStart,
                    end = baseHorizontal,
                    top = if (isSubSection) 8.dp else 12.dp,
                    bottom = if (isSubSection) 8.dp else 12.dp,
                ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = kind.icon(),
                contentDescription = null,
                modifier = Modifier.size(if (isSubSection) 20.dp else 22.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = title,
                style = if (isSubSection) {
                    MaterialTheme.typography.titleMedium
                } else {
                    MaterialTheme.typography.titleLarge
                },
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (trailing != null) {
                Spacer(Modifier.weight(1f))
                // Un petit espace visuel entre le titre et l'horloge.
                Spacer(Modifier.width(6.dp))
                trailing()
            }
        }
    }
}
