package com.notesdusecouriste.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private fun terrainExpressiveLight() = expressiveLightColorScheme().copy(
    primary = AccentBlue,
    onPrimary = Color.White,
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimaryLight,
    surface = SurfaceLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = TextSecondaryLight,
    error = AlertCritical,
)

private fun terrainExpressiveDark() = darkColorScheme().copy(
    primary = AccentBlue,
    onPrimary = Color.White,
    secondary = SecondaryBlue,
    onSecondary = Color.White,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = TextSecondary,
    error = AlertCritical,
)

/** Thème racine Material 3 Expressive — palette terrain + shapes/motion expressifs par défaut. */
@Composable
fun NotesDuSecouristeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialExpressiveTheme(
        colorScheme = if (darkTheme) terrainExpressiveDark() else terrainExpressiveLight(),
        shapes = ExpressiveShapes,
        content = content,
    )
}
