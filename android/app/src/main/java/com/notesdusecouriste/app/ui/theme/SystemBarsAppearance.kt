package com.notesdusecouriste.app.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Syncs status/navigation bar icon contrast with in-app theme (not only system uiMode).
 * Dark theme → light icons; light theme → dark icons.
 */
@Composable
fun SystemBarsAppearance(isDarkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val controller = WindowCompat.getInsetsController(window, view)
            val lightBackground = !isDarkTheme
            controller.isAppearanceLightStatusBars = lightBackground
            controller.isAppearanceLightNavigationBars = lightBackground
        }
    }
}
