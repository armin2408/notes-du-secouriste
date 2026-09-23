package com.notesdusecouriste.core.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// —— Identité terrain (primary) — vert logo #02A459 ——
val AccentGreen = Color(0xFF02A459)
val AccentBlue = AccentGreen // alias historique
val SecondaryBlue = AccentGreen

// —— Palette claire ——
val TerrainPrimary = Color(0xFF02A459)
val TerrainOnPrimary = Color(0xFFFFFFFF)
val TerrainPrimaryContainer = Color(0xFFD4F5E4)
val TerrainOnPrimaryContainer = Color(0xFF003D22)

val TerrainSecondary = Color(0xFF4A6358)
val TerrainOnSecondary = Color(0xFFFFFFFF)
val TerrainSecondaryContainer = Color(0xFFE4EFE8)
val TerrainOnSecondaryContainer = Color(0xFF25362E)

val TerrainTertiary = Color(0xFF3B6B52)
val TerrainOnTertiary = Color(0xFFFFFFFF)
val TerrainTertiaryContainer = Color(0xFFD6F0E2)
val TerrainOnTertiaryContainer = Color(0xFF143528)

val TerrainBackground = Color(0xFFF5F9F6)
val TerrainOnBackground = Color(0xFF15201A)
val TerrainSurface = Color(0xFFFFFFFF)
val TerrainOnSurface = Color(0xFF15201A)
val TerrainSurfaceVariant = Color(0xFFEAF3EE)
val TerrainOnSurfaceVariant = Color(0xFF556B5E)

val TerrainSurfaceContainerLowest = Color(0xFFFFFFFF)
val TerrainSurfaceContainerLow = Color(0xFFF1F7F3)
val TerrainSurfaceContainer = Color(0xFFEAF3EE)
val TerrainSurfaceContainerHigh = Color(0xFFE2EEE7)
val TerrainSurfaceContainerHighest = Color(0xFFD9E8DF)

val TerrainOutline = Color(0xFFC5D4CB)
val TerrainOutlineVariant = Color(0xFFD8E5DC)

val TerrainError = Color(0xFFB3261E)
val TerrainOnError = Color(0xFFFFFFFF)
val TerrainErrorContainer = Color(0xFFFFE0E0)
val TerrainOnErrorContainer = Color(0xFF7A1A16)

// —— Palette sombre ——
val TerrainDarkBackground = Color(0xFF0F1412)
val TerrainDarkOnBackground = Color(0xFFE6EDE8)
val TerrainDarkSurface = Color(0xFF151C18)
val TerrainDarkOnSurface = Color(0xFFE6EDE8)
val TerrainDarkSurfaceVariant = Color(0xFF1E2A23)
val TerrainDarkOnSurfaceVariant = Color(0xFFA8B9AF)

val TerrainDarkSurfaceContainerLowest = Color(0xFF0B100E)
val TerrainDarkSurfaceContainerLow = Color(0xFF1A221D)
val TerrainDarkSurfaceContainer = Color(0xFF1E2A23)
val TerrainDarkSurfaceContainerHigh = Color(0xFF28352D)
val TerrainDarkSurfaceContainerHighest = Color(0xFF324038)

val TerrainDarkPrimary = Color(0xFF6BD9A0)
val TerrainDarkOnPrimary = Color(0xFF003D22)
val TerrainDarkPrimaryContainer = Color(0xFF005C35)
val TerrainDarkOnPrimaryContainer = Color(0xFFD4F5E4)

val TerrainDarkSecondary = Color(0xFFA8C0B3)
val TerrainDarkOnSecondary = Color(0xFF173028)
val TerrainDarkSecondaryContainer = Color(0xFF2A3B33)
val TerrainDarkOnSecondaryContainer = Color(0xFFD5E7DC)

val TerrainDarkTertiary = Color(0xFF8FD0B0)
val TerrainDarkOnTertiary = Color(0xFF003825)
val TerrainDarkTertiaryContainer = Color(0xFF264A38)
val TerrainDarkOnTertiaryContainer = Color(0xFFB8EAD3)

val TerrainDarkOutline = Color(0xFF6F8177)
val TerrainDarkOutlineVariant = Color(0xFF3D4C43)

val TerrainDarkError = Color(0xFFFFB4AB)
val TerrainDarkOnError = Color(0xFF690005)
val TerrainDarkErrorContainer = Color(0xFF93000A)
val TerrainDarkOnErrorContainer = Color(0xFFFFDAD6)

// —— Couleurs métier / mesures (clair) ——
val MeasureNormalContainer = Color(0xFFD4F5E4)
val MeasureNormalOnContainer = Color(0xFF017A44)
val MeasureWatchContainer = Color(0xFFFFF1C2)
val MeasureWatchOnContainer = Color(0xFF765A00)
val MeasureAlertContainer = Color(0xFFFFE0C2)
val MeasureAlertOnContainer = Color(0xFFA94D00)
val MeasureCriticalContainer = Color(0xFFFFE0E0)
val MeasureCriticalOnContainer = Color(0xFFB3261E)

// —— Couleurs métier / mesures (sombre) ——
val MeasureNormalContainerDark = Color(0xFF0F3D27)
val MeasureNormalOnContainerDark = Color(0xFF6BD9A0)
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

val StatusDraft = Color(0xFF02A459)
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
