package com.notesdusecouriste.core.ui.systembars

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Insets Scaffold sans la barre de navigation : le contenu peut défiler dessous
 * pour alimenter le flou système (mode 3 boutons).
 */
@Composable
fun scaffoldContentWithoutNavigationBar(): WindowInsets =
    WindowInsets.statusBars
        .union(WindowInsets.displayCutout)
        .union(WindowInsets.ime)

@Composable
fun navigationBarBottomPadding(extra: Dp = 0.dp): Dp {
    val density = LocalDensity.current
    return with(density) { WindowInsets.navigationBars.getBottom(this).toDp() } + extra
}
