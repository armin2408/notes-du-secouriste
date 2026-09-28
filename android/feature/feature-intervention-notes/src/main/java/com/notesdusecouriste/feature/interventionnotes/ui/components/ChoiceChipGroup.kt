package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.core.ui.theme.LocalNotesSemanticColors

enum class ChoiceTone {
    Green,
    Yellow,
    Orange,
}

data class ChoiceOption(
    val key: String,
    val label: String,
    val tone: ChoiceTone,
)

private val segmentOuterRadius = 24.dp
private val segmentInnerRadius = 6.dp
private val segmentGap = 2.dp
private val segmentMinHeight = 48.dp

/**
 * Choix unique — liste horizontale segmentée (Material 3 Expressive) : segments séparés,
 * coins intérieurs serrés, le segment sélectionné s'arrondit en pilule.
 */
@Composable
fun ChoiceChipGroup(
    label: String,
    options: List<ChoiceOption>,
    selectedKey: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
    horizontalScroll: Boolean = true,
    boldFirstLetter: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FieldLabelText(
            label = label,
            boldFirstLetter = boldFirstLetter,
            modifier = Modifier.padding(start = 4.dp),
        )
        Row(
            modifier = if (horizontalScroll) {
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            } else {
                Modifier
            },
            horizontalArrangement = Arrangement.spacedBy(segmentGap),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            options.forEachIndexed { index, option ->
                ChoiceSegment(
                    option = option,
                    selected = selectedKey == option.key,
                    isFirstOption = index == 0,
                    segmentIndex = index,
                    segmentCount = options.size,
                    onSelect = {
                        if (selectedKey == option.key) {
                            onSelect("")
                        } else {
                            onSelect(option.key)
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun ChoiceSegment(
    option: ChoiceOption,
    selected: Boolean,
    isFirstOption: Boolean,
    segmentIndex: Int,
    segmentCount: Int,
    onSelect: () -> Unit,
) {
    val tone = selectedToneColors(isFirstOption)
    val containerColor by animateColorAsState(
        if (selected) tone.container else MaterialTheme.colorScheme.surfaceContainerHighest,
        label = "segmentContainer",
    )
    val contentColor by animateColorAsState(
        if (selected) tone.onContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "segmentContent",
    )
    val isStart = segmentIndex == 0
    val isEnd = segmentIndex == segmentCount - 1
    val start by animateSegmentCorner(selected || isStart)
    val end by animateSegmentCorner(selected || isEnd)

    Surface(
        modifier = Modifier
            .defaultMinSize(minHeight = segmentMinHeight)
            .selectable(
                selected = selected,
                role = Role.RadioButton,
                onClick = onSelect,
            ),
        shape = RoundedCornerShape(
            topStart = start,
            bottomStart = start,
            topEnd = end,
            bottomEnd = end,
        ),
        color = containerColor,
        contentColor = contentColor,
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = segmentMinHeight)
                .padding(horizontal = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text = option.label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun animateSegmentCorner(rounded: Boolean) = animateDpAsState(
    targetValue = if (rounded) segmentOuterRadius else segmentInnerRadius,
    animationSpec = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium,
    ),
    label = "segmentCorner",
)

private data class ToneColors(val container: Color, val onContainer: Color)

/**
 * Teintes pleines (foncées) : on inverse le couple container / onContainer de la palette
 * sémantique pour un contraste fort, en clair comme en sombre.
 */
@Composable
private fun selectedToneColors(isFirstOption: Boolean): ToneColors {
    val semantic = LocalNotesSemanticColors.current
    return if (isFirstOption) {
        ToneColors(container = semantic.normalOnContainer, onContainer = semantic.normalContainer)
    } else {
        ToneColors(container = semantic.watchStrong, onContainer = semantic.onWatchStrong)
    }
}