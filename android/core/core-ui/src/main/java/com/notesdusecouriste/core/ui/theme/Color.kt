package com.notesdusecouriste.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// —— Identité terrain (primary) ——
val AccentBlue = Color(0xFF1565C0)
val SecondaryBlue = AccentBlue

// —— Palette claire ——
val TerrainPrimary = Color(0xFF1565C0)
val TerrainOnPrimary = Color(0xFFFFFFFF)
val TerrainPrimaryContainer = Color(0xFFE3F0FF)
val TerrainOnPrimaryContainer = Color(0xFF0D3A6E)

val TerrainSecondary = Color(0xFF4E667A)
val TerrainOnSecondary = Color(0xFFFFFFFF)
val TerrainSecondaryContainer = Color(0xFFE7EEF5)
val TerrainOnSecondaryContainer = Color(0xFF263746)

val TerrainTertiary = Color(0xFF3F6F6A)
val TerrainOnTertiary = Color(0xFFFFFFFF)
val TerrainTertiaryContainer = Color(0xFFDCEFEB)
val TerrainOnTertiaryContainer = Color(0xFF173E3A)

val TerrainBackground = Color(0xFFF6F8FB)
val TerrainOnBackground = Color(0xFF18212B)
val TerrainSurface = Color(0xFFFFFFFF)
val TerrainOnSurface = Color(0xFF18212B)
val TerrainSurfaceVariant = Color(0xFFEEF3F8)
val TerrainOnSurfaceVariant = Color(0xFF5B6875)

val TerrainSurfaceContainerLowest = Color(0xFFFFFFFF)
val TerrainSurfaceContainerLow = Color(0xFFF3F6F9)
val TerrainSurfaceContainer = Color(0xFFEEF3F8)
val TerrainSurfaceContainerHigh = Color(0xFFE7EDF3)
val TerrainSurfaceContainerHighest = Color(0xFFE0E7EE)

val TerrainOutline = Color(0xFFCBD4DD)
val TerrainOutlineVariant = Color(0xFFDDE4EA)

val TerrainError = Color(0xFFB3261E)
val TerrainOnError = Color(0xFFFFFFFF)
val TerrainErrorContainer = Color(0xFFFFE0E0)
val TerrainOnErrorContainer = Color(0xFF7A1A16)

// —— Palette sombre ——
val TerrainDarkBackground = Color(0xFF101418)
val TerrainDarkOnBackground = Color(0xFFE7EDF3)
val TerrainDarkSurface = Color(0xFF161B21)
val TerrainDarkOnSurface = Color(0xFFE7EDF3)
val TerrainDarkSurfaceVariant = Color(0xFF202832)
val TerrainDarkOnSurfaceVariant = Color(0xFFAEB9C5)

val TerrainDarkSurfaceContainerLowest = Color(0xFF0C1014)
val TerrainDarkSurfaceContainerLow = Color(0xFF1B2128)
val TerrainDarkSurfaceContainer = Color(0xFF202832)
val TerrainDarkSurfaceContainerHigh = Color(0xFF2A333E)
val TerrainDarkSurfaceContainerHighest = Color(0xFF343E4A)

val TerrainDarkPrimary = Color(0xFF7EB8FF)
val TerrainDarkOnPrimary = Color(0xFF00325A)
val TerrainDarkPrimaryContainer = Color(0xFF0F3D67)
val TerrainDarkOnPrimaryContainer = Color(0xFFD7E9FF)

val TerrainDarkSecondary = Color(0xFFA7BACB)
val TerrainDarkOnSecondary = Color(0xFF17303F)
val TerrainDarkSecondaryContainer = Color(0xFF2A3946)
val TerrainDarkOnSecondaryContainer = Color(0xFFD7E3ED)

val TerrainDarkTertiary = Color(0xFF8FC9C1)
val TerrainDarkOnTertiary = Color(0xFF00382F)
val TerrainDarkTertiaryContainer = Color(0xFF264943)
val TerrainDarkOnTertiaryContainer = Color(0xFFB8EAE3)

val TerrainDarkOutline = Color(0xFF6F7B86)
val TerrainDarkOutlineVariant = Color(0xFF3D4852)

val TerrainDarkError = Color(0xFFFFB4AB)
val TerrainDarkOnError = Color(0xFF690005)
val TerrainDarkErrorContainer = Color(0xFF93000A)
val TerrainDarkOnErrorContainer = Color(0xFFFFDAD6)

// —— Couleurs métier / mesures (clair) ——
val MeasureNormalContainer = Color(0xFFDDF3E2)
val MeasureNormalOnContainer = Color(0xFF1E6B35)
val MeasureWatchContainer = Color(0xFFFFF1C2)
val MeasureWatchOnContainer = Color(0xFF765A00)
val MeasureAlertContainer = Color(0xFFFFE0C2)
val MeasureAlertOnContainer = Color(0xFFA94D00)
val MeasureCriticalContainer = Color(0xFFFFE0E0)
val MeasureCriticalOnContainer = Color(0xFFB3261E)

// —— Couleurs métier / mesures (sombre) ——
val MeasureNormalContainerDark = Color(0xFF173C27)
val MeasureNormalOnContainerDark = Color(0xFF9BD8AE)
val MeasureWatchContainerDark = Color(0xFF473A10)
val MeasureWatchOnContainerDark = Color(0xFFF6D86B)
val MeasureAlertContainerDark = Color(0xFF4A2B13)
val MeasureAlertOnContainerDark = Color(0xFFFFB874)
val MeasureCriticalContainerDark = Color(0xFF4B1F21)
val MeasureCriticalOnContainerDark = Color(0xFFFFB4AB)

/** Alias historiques — valeurs claires (PDF / défauts hors CompositionLocal). */
val ChoiceSelectedGreenContainer = MeasureNormalContainer
val ChoiceSelectedGreenOnContainer = MeasureNormalOnContainer
val ChoiceSelectedYellowContainer = MeasureWatchContainer
val ChoiceSelectedYellowOnContainer = MeasureWatchOnContainer
val ChoiceSelectedOrangeContainer = MeasureAlertContainer
val ChoiceSelectedOrangeOnContainer = MeasureAlertOnContainer

val TemperatureOkGreen = MeasureNormalOnContainer
val TemperatureWarnOrange = MeasureAlertOnContainer

val AlertWarning = MeasureWatchOnContainer
val AlertCritical = MeasureCriticalOnContainer
val PrimaryRed = TerrainError

val StatusDraft = Color(0xFF42A5F5)
val StatusClosed = TerrainSecondary

val BackgroundLight = TerrainBackground
val SurfaceLight = TerrainSurface
val TextPrimaryLight = TerrainOnSurface
val TextSecondaryLight = TerrainOnSurfaceVariant
val BackgroundDark = TerrainDarkBackground
val SurfaceDark = TerrainDarkSurface
val TextPrimary = TerrainDarkOnSurface
val TextSecondary = TerrainDarkOnSurfaceVariant

@Immutable
data class NotesSemanticColors(
    val normalContainer: Color,
    val normalOnContainer: Color,
    val watchContainer: Color,
    val watchOnContainer: Color,
    val alertContainer: Color,
    val alertOnContainer: Color,
    val criticalContainer: Color,
    val criticalOnContainer: Color,
)

val LightNotesSemanticColors = NotesSemanticColors(
    normalContainer = MeasureNormalContainer,
    normalOnContainer = MeasureNormalOnContainer,
    watchContainer = MeasureWatchContainer,
    watchOnContainer = MeasureWatchOnContainer,
    alertContainer = MeasureAlertContainer,
    alertOnContainer = MeasureAlertOnContainer,
    criticalContainer = MeasureCriticalContainer,
    criticalOnContainer = MeasureCriticalOnContainer,
)

val DarkNotesSemanticColors = NotesSemanticColors(
    normalContainer = MeasureNormalContainerDark,
    normalOnContainer = MeasureNormalOnContainerDark,
    watchContainer = MeasureWatchContainerDark,
    watchOnContainer = MeasureWatchOnContainerDark,
    alertContainer = MeasureAlertContainerDark,
    alertOnContainer = MeasureAlertOnContainerDark,
    criticalContainer = MeasureCriticalContainerDark,
    criticalOnContainer = MeasureCriticalOnContainerDark,
)

val LocalNotesSemanticColors = staticCompositionLocalOf { LightNotesSemanticColors }
