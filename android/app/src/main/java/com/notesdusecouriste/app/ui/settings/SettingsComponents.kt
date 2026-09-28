package com.notesdusecouriste.app.ui.settings

import android.content.Context
import android.os.Build
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.app.R

private val SegmentOuterRadius = 20.dp
private val SegmentInnerRadius = 4.dp
internal val SettingsSegmentGap = 2.dp

@Composable
internal fun SettingsSectionHeader(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp),
    )
}

/** Forme d'un élément de liste segmentée (Material 3 Expressive) selon sa position. */
@Composable
internal fun segmentShape(index: Int, count: Int): RoundedCornerShape {
    val top = if (index == 0) SegmentOuterRadius else SegmentInnerRadius
    val bottom = if (index == count - 1) SegmentOuterRadius else SegmentInnerRadius
    return RoundedCornerShape(topStart = top, topEnd = top, bottomStart = bottom, bottomEnd = bottom)
}

/** Groupe vertical d'éléments segmentés séparés de 2 dp. */
@Composable
internal fun SettingsSegmentedGroup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SettingsSegmentGap),
    ) {
        content()
    }
}

@Composable
internal fun SettingsSegmentedItem(
    icon: ImageVector,
    title: String,
    description: String?,
    index: Int,
    count: Int,
    onClick: () -> Unit,
    trailingIcon: ImageVector? = null,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = segmentShape(index, count),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .heightIn(min = 72.dp)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SettingsLeadingIcon(icon)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (description != null) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            if (trailingIcon != null) {
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
internal fun SettingsLeadingIcon(icon: ImageVector) {
    Surface(
        modifier = Modifier.size(40.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(22.dp))
        }
    }
}

/**
 * Boutons connectés (Material 3 Expressive) : segments séparés, le segment sélectionné
 * s'arrondit en pilule.
 */
@Composable
internal fun ConnectedChoiceButtons(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SettingsSegmentGap),
    ) {
        options.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            val start by animateCorner(selected || index == 0)
            val end by animateCorner(selected || index == options.lastIndex)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                shape = RoundedCornerShape(
                    topStart = start,
                    bottomStart = start,
                    topEnd = end,
                    bottomEnd = end,
                ),
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.surfaceContainerHighest
                },
                contentColor = if (selected) {
                    MaterialTheme.colorScheme.onPrimary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                onClick = { onSelect(index) },
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun animateCorner(rounded: Boolean) = animateDpAsState(
    targetValue = if (rounded) 24.dp else 8.dp,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    ),
    label = "connectedCorner",
)

@Composable
internal fun AppVersionFooter(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val version = remember(context) { appVersion(context) }
    Text(
        text = stringResource(R.string.settings_version, version.first, version.second),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
        textAlign = TextAlign.Center,
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 16.dp),
    )
}

private fun appVersion(context: Context): Pair<String, Long> {
    val info = context.packageManager.getPackageInfo(context.packageName, 0)
    val code = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        info.longVersionCode
    } else {
        @Suppress("DEPRECATION")
        info.versionCode.toLong()
    }
    return (info.versionName ?: "—") to code
}
