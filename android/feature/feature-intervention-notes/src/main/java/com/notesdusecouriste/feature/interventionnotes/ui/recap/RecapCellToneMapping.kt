package com.notesdusecouriste.feature.interventionnotes.ui.recap

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.notesdusecouriste.core.ui.theme.LocalNotesSemanticColors
import com.notesdusecouriste.feature.interventionnotes.ui.components.ChoiceTone

internal data class RecapToneColors(
    val container: Color,
    val onContainer: Color,
)

internal fun ChoiceTone.toRecapCellTone(): RecapCellTone =
    when (this) {
        ChoiceTone.Green -> RecapCellTone.Green
        ChoiceTone.Yellow -> RecapCellTone.Yellow
        ChoiceTone.Orange -> RecapCellTone.Orange
    }

@Composable
internal fun RecapCellTone.toColors(): RecapToneColors? {
    val semantic = LocalNotesSemanticColors.current
    return when (this) {
        RecapCellTone.None -> null
        RecapCellTone.Green -> RecapToneColors(
            container = semantic.normalContainer,
            onContainer = semantic.normalOnContainer,
        )
        RecapCellTone.Yellow -> RecapToneColors(
            container = semantic.watchContainer,
            onContainer = semantic.watchOnContainer,
        )
        RecapCellTone.Orange -> RecapToneColors(
            container = semantic.alertContainer,
            onContainer = semantic.alertOnContainer,
        )
    }
}
