package com.notesdusecouriste.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

private val ColorScrimDark = Color.Black.copy(alpha = 0.48f)

/**
 * Schémas complets (sans héritage expressive defaults) pour éliminer toute teinte violette/lilas
 * tout en conservant MaterialExpressiveTheme pour shapes / motion.
 */
private fun terrainLightColorScheme() = lightColorScheme(
    primary = TerrainPrimary,
    onPrimary = TerrainOnPrimary,
    primaryContainer = TerrainPrimaryContainer,
    onPrimaryContainer = TerrainOnPrimaryContainer,
    secondary = TerrainSecondary,
    onSecondary = TerrainOnSecondary,
    secondaryContainer = TerrainSecondaryContainer,
    onSecondaryContainer = TerrainOnSecondaryContainer,
    tertiary = TerrainTertiary,
    onTertiary = TerrainOnTertiary,
    tertiaryContainer = TerrainTertiaryContainer,
    onTertiaryContainer = TerrainOnTertiaryContainer,
    error = TerrainError,
    onError = TerrainOnError,
    errorContainer = TerrainErrorContainer,
    onErrorContainer = TerrainOnErrorContainer,
    background = TerrainBackground,
    onBackground = TerrainOnBackground,
    surface = TerrainSurface,
    onSurface = TerrainOnSurface,
    surfaceVariant = TerrainSurfaceVariant,
    onSurfaceVariant = TerrainOnSurfaceVariant,
    surfaceTint = TerrainPrimary,
    inverseSurface = TerrainDarkSurface,
    inverseOnSurface = TerrainDarkOnSurface,
    inversePrimary = TerrainDarkPrimary,
    outline = TerrainOutline,
    outlineVariant = TerrainOutlineVariant,
    scrim = TerrainOnBackground.copy(alpha = 0.32f),
    surfaceBright = TerrainSurface,
    surfaceDim = TerrainSurfaceContainerHigh,
    surfaceContainerLowest = TerrainSurfaceContainerLowest,
    surfaceContainerLow = TerrainSurfaceContainerLow,
    surfaceContainer = TerrainSurfaceContainer,
    surfaceContainerHigh = TerrainSurfaceContainerHigh,
    surfaceContainerHighest = TerrainSurfaceContainerHighest,
)

private fun terrainDarkColorScheme() = darkColorScheme(
    primary = TerrainDarkPrimary,
    onPrimary = TerrainDarkOnPrimary,
    primaryContainer = TerrainDarkPrimaryContainer,
    onPrimaryContainer = TerrainDarkOnPrimaryContainer,
    secondary = TerrainDarkSecondary,
    onSecondary = TerrainDarkOnSecondary,
    secondaryContainer = TerrainDarkSecondaryContainer,
    onSecondaryContainer = TerrainDarkOnSecondaryContainer,
    tertiary = TerrainDarkTertiary,
    onTertiary = TerrainDarkOnTertiary,
    tertiaryContainer = TerrainDarkTertiaryContainer,
    onTertiaryContainer = TerrainDarkOnTertiaryContainer,
    error = TerrainDarkError,
    onError = TerrainDarkOnError,
    errorContainer = TerrainDarkErrorContainer,
    onErrorContainer = TerrainDarkOnErrorContainer,
    background = TerrainDarkBackground,
    onBackground = TerrainDarkOnBackground,
    surface = TerrainDarkSurface,
    onSurface = TerrainDarkOnSurface,
    surfaceVariant = TerrainDarkSurfaceVariant,
    onSurfaceVariant = TerrainDarkOnSurfaceVariant,
    surfaceTint = TerrainDarkPrimary,
    inverseSurface = TerrainSurfaceContainer,
    inverseOnSurface = TerrainOnSurface,
    inversePrimary = TerrainPrimary,
    outline = TerrainDarkOutline,
    outlineVariant = TerrainDarkOutlineVariant,
    scrim = ColorScrimDark,
    surfaceBright = TerrainDarkSurfaceContainerHighest,
    surfaceDim = TerrainDarkBackground,
    surfaceContainerLowest = TerrainDarkSurfaceContainerLowest,
    surfaceContainerLow = TerrainDarkSurfaceContainerLow,
    surfaceContainer = TerrainDarkSurfaceContainer,
    surfaceContainerHigh = TerrainDarkSurfaceContainerHigh,
    surfaceContainerHighest = TerrainDarkSurfaceContainerHighest,
)

/** Thème racine Material 3 Expressive — palette terrain + shapes/motion expressifs. */
@Composable
fun NotesDuSecouristeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val semantic = if (darkTheme) DarkNotesSemanticColors else LightNotesSemanticColors
    CompositionLocalProvider(LocalNotesSemanticColors provides semantic) {
        MaterialExpressiveTheme(
            colorScheme = if (darkTheme) terrainDarkColorScheme() else terrainLightColorScheme(),
            shapes = ExpressiveShapes,
            content = content,
        )
    }
}
