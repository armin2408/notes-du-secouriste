package com.notesdusecouriste.app

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.notesdusecouriste.app.navigation.AppNavHost
import com.notesdusecouriste.app.shortcuts.AppShortcut
import com.notesdusecouriste.app.ui.theme.AppThemeHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingShortcut by mutableStateOf<AppShortcut?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            ),
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT,
            ),
        )
        // Pas de relance du raccourci après une recréation (rotation, changement de thème…).
        if (savedInstanceState == null) {
            pendingShortcut = AppShortcut.from(intent)
        }
        setContent {
            AppThemeHost {
                AppNavHost(
                    pendingShortcut = pendingShortcut,
                    onShortcutHandled = { pendingShortcut = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        AppShortcut.from(intent)?.let { pendingShortcut = it }
    }
}
