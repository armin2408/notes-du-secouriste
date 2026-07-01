package com.notesdusecouriste.app.ui.theme

import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notesdusecouriste.core.ui.systembars.EdgeToEdgeRoot
import com.notesdusecouriste.core.ui.theme.NotesDuSecouristeTheme

/** Single Activity-scoped instance — must match [AppThemeHost] (not nav-route scoped). */
@Composable
fun appThemeViewModel(): AppThemeViewModel {
    val activity = LocalContext.current as ComponentActivity
    return hiltViewModel(viewModelStoreOwner = activity)
}

@Composable
fun AppThemeHost(
    viewModel: AppThemeViewModel = appThemeViewModel(),
    content: @Composable () -> Unit,
) {
    val isSystemDark = isSystemInDarkTheme()
    LaunchedEffect(isSystemDark) {
        viewModel.updateSystemDark(isSystemDark)
    }
    val isDark by viewModel.effectiveIsDark.collectAsStateWithLifecycle()

    NotesDuSecouristeTheme(darkTheme = isDark) {
        SystemBarsAppearance(isDarkTheme = isDark)
        EdgeToEdgeRoot {
            content()
        }
    }
}
