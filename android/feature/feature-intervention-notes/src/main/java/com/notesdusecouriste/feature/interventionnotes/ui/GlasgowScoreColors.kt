package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import com.notesdusecouriste.core.data.model.GlasgowMesure
import com.notesdusecouriste.core.ui.theme.LocalNotesSemanticColors

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
fun GlasgowScoreSeverity.colors(): GlasgowScoreColors {
    val semantic = LocalNotesSemanticColors.current
    return when (this) {
        GlasgowScoreSeverity.Neutral -> GlasgowScoreColors(
            container = MaterialTheme.colorScheme.surfaceContainerHighest,
            onContainer = MaterialTheme.colorScheme.onSurface,
        )
        GlasgowScoreSeverity.Green -> GlasgowScoreColors(
            container = semantic.normalContainer,
            onContainer = semantic.normalOnContainer,
        )
        GlasgowScoreSeverity.Orange -> GlasgowScoreColors(
            container = semantic.alertContainer,
            onContainer = semantic.alertOnContainer,
        )
        GlasgowScoreSeverity.Red -> GlasgowScoreColors(
            container = semantic.criticalContainer,
            onContainer = semantic.criticalOnContainer,
        )
    }
}
