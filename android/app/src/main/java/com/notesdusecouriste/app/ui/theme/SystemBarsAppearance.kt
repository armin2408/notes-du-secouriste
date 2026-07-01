package com.notesdusecouriste.app.ui.theme

import android.graphics.Color
import android.os.Build
import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Syncs status/navigation bar icon contrast with in-app theme (not only system uiMode).
 * Dark theme → light icons; light theme → dark icons.
 *
 * En navigation 3 boutons, désactive le scrim système pour laisser place au flou applicatif.
 */
@Composable
fun SystemBarsAppearance(isDarkTheme: Boolean) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.navigationBarColor = Color.TRANSPARENT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
            val controller = WindowCompat.getInsetsController(window, view)
            val lightBackground = !isDarkTheme
            controller.isAppearanceLightStatusBars = lightBackground
            controller.isAppearanceLightNavigationBars = lightBackground
        }
    }
}
