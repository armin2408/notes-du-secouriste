package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.notesdusecouriste.core.data.model.GlasgowMesure
import com.notesdusecouriste.core.ui.theme.AlertCritical
import com.notesdusecouriste.core.ui.theme.AlertWarning
import com.notesdusecouriste.core.ui.theme.ChoiceSelectedGreenContainer
import com.notesdusecouriste.core.ui.theme.ChoiceSelectedGreenOnContainer

enum class GlasgowScoreSeverity {
    Neutral,
    Green,
    Orange,
    Red,
}

fun GlasgowMesure.scoreSeverity(): GlasgowScoreSeverity {
    val score = totalScore() ?: return GlasgowScoreSeverity.Neutral
    return when (score) {
        in 13..15 -> GlasgowScoreSeverity.Green
        in 9..12 -> GlasgowScoreSeverity.Orange
        else -> GlasgowScoreSeverity.Red
    }
}

@Immutable
data class GlasgowScoreColors(
    val container: Color,
    val onContainer: Color,
)

@Composable
fun GlasgowScoreSeverity.colors(): GlasgowScoreColors =
    when (this) {
        GlasgowScoreSeverity.Neutral -> GlasgowScoreColors(
            container = MaterialTheme.colorScheme.surfaceContainerHighest,
            onContainer = MaterialTheme.colorScheme.onSurface,
        )
        GlasgowScoreSeverity.Green -> GlasgowScoreColors(
            container = ChoiceSelectedGreenContainer,
            onContainer = ChoiceSelectedGreenOnContainer,
        )
        GlasgowScoreSeverity.Orange -> GlasgowScoreColors(
            container = AlertWarning.copy(alpha = 0.35f),
            onContainer = Color(0xFFE65100),
        )
        GlasgowScoreSeverity.Red -> GlasgowScoreColors(
            container = AlertCritical.copy(alpha = 0.28f),
            onContainer = Color(0xFFB71C1C),
        )
    }
