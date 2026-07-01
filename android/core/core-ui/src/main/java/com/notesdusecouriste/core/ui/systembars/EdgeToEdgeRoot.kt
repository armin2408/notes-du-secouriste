package com.notesdusecouriste.core.ui.systembars

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState

/**
 * Enveloppe edge-to-edge : le contenu peut défiler sous la barre de navigation.
 * En mode 3 boutons (retour / accueil / applis), une zone floutée recouvre la barre système.
 */
@Composable
fun EdgeToEdgeRoot(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val hazeState = rememberHazeState()
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState),
            content = content,
        )
        ThreeButtonNavigationBarBlur(hazeState = hazeState)
    }
}

@Composable
private fun BoxScope.ThreeButtonNavigationBarBlur(
    hazeState: dev.chrisbanes.haze.HazeState,
) {
    val density = LocalDensity.current
    val tappableBottomPx = WindowInsets.tappableElement.getBottom(density)
    if (tappableBottomPx <= 0) return

    val navBarHeight = with(density) { tappableBottomPx.toDp() }
    if (navBarHeight <= 0.dp) return

    val blurStyle = HazeStyle(
        blurRadius = 14.dp,
        backgroundColor = Color.Transparent,
        tint = HazeTint(Color.Transparent),
    )

    Box(
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .fillMaxWidth()
            .height(navBarHeight)
            .hazeEffect(state = hazeState, style = blurStyle),
    )
}
