package com.notesdusecouriste.feature.interventionnotes.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.shape.RoundedCornerShape
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

private val expressiveSegmentRadius = 20.dp
private val expressiveSegmentInnerRadius = 2.dp

/** Choix unique — groupe de pills connectées (Material 3 Expressive). */
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
        val connectedGroup = @Composable {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(IntrinsicSize.Min),
                shape = RoundedCornerShape(expressiveSegmentRadius),
                color = MaterialTheme.colorScheme.surfaceContainerHighest,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    options.forEachIndexed { index, option ->
                        val selected = selectedKey == option.key
                        if (index > 0) {
                            ExpressiveSegmentDivider()
                        }
                        ChoiceListItemHorizontal(
                            option = option,
                            selected = selected,
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
        if (horizontalScroll) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
            ) {
                connectedGroup()
            }
        } else {
            connectedGroup()
        }
    }
}

@Composable
private fun ExpressiveSegmentDivider() {
    VerticalDivider(
        modifier = Modifier
            .fillMaxHeight()
            .width(1.dp),
        color = MaterialTheme.colorScheme.outlineVariant,
    )
}

@Composable
private fun ChoiceListItemHorizontal(
    option: ChoiceOption,
    selected: Boolean,
    isFirstOption: Boolean,
    segmentIndex: Int,
    segmentCount: Int,
    onSelect: () -> Unit,
) {
    val toneColors = if (selected) {
        selectedListColors(isFirstOption)
    } else {
        unselectedListColors()
    }
    val shape = expressiveSegmentShape(segmentIndex, segmentCount)
    Surface(
        modifier = Modifier
            .defaultMinSize(minHeight = 44.dp)
            .clickable(onClick = onSelect),
        shape = shape,
        color = if (selected) toneColors.container else Color.Transparent,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = toneColors.onContainer,
                )
            }
            Text(
                text = option.label,
                style = MaterialTheme.typography.bodyLarge,
                color = if (selected) toneColors.onContainer else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

private fun expressiveSegmentShape(index: Int, count: Int): Shape {
    val outer = expressiveSegmentRadius
    val inner = expressiveSegmentInnerRadius
    return when {
        count == 1 -> RoundedCornerShape(outer)
        index == 0 -> RoundedCornerShape(
            topStart = outer,
            bottomStart = outer,
            topEnd = inner,
            bottomEnd = inner,
        )
        index == count - 1 -> RoundedCornerShape(
            topStart = inner,
            bottomStart = inner,
            topEnd = outer,
            bottomEnd = outer,
        )
        else -> RoundedCornerShape(inner)
    }
}

private data class ListToneColors(val container: Color, val onContainer: Color)

@Composable
private fun selectedListColors(isFirstOption: Boolean): ListToneColors {
    val semantic = LocalNotesSemanticColors.current
    return if (isFirstOption) {
        ListToneColors(
            container = semantic.normalContainer,
            onContainer = semantic.normalOnContainer,
        )
    } else {
        ListToneColors(
            container = semantic.watchContainer,
            onContainer = semantic.watchOnContainer,
        )
    }
}

@Composable
private fun unselectedListColors(): ListToneColors =
    ListToneColors(
        container = MaterialTheme.colorScheme.surfaceContainerHighest,
        onContainer = MaterialTheme.colorScheme.onSurface,
    )
