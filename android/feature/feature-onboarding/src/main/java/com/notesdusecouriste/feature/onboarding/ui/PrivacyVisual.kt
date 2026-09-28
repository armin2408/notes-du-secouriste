package com.notesdusecouriste.feature.onboarding.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.NoAccounts
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.notesdusecouriste.feature.onboarding.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private data class PrivacyBadge(
    val icon: ImageVector,
    val labelRes: Int,
    val xRatio: Float,
    val yRatio: Float,
)

private val Badges = listOf(
    PrivacyBadge(Icons.Outlined.PhoneAndroid, R.string.welcome_privacy_local, -1f, -1f),
    PrivacyBadge(Icons.Outlined.NoAccounts, R.string.welcome_privacy_no_account, 1f, -1f),
    PrivacyBadge(Icons.Outlined.CloudOff, R.string.welcome_privacy_no_cloud, -1f, 1f),
    PrivacyBadge(Icons.Outlined.PrivacyTip, R.string.welcome_privacy_consent, 1f, 1f),
)

private val DesignWidth = 300.dp
private val DesignHeight = 280.dp

/**
 * Bouclier central et pastilles « local / sans compte / sans serveur / vie privée ».
 */
@Composable
internal fun PrivacyVisual(
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val shieldAppear = remember { Animatable(0f) }
    val badgeAppear = remember { Badges.map { Animatable(0f) } }
    LaunchedEffect(isActive) {
        if (!isActive || shieldAppear.value != 0f) return@LaunchedEffect
        launch {
            shieldAppear.animateTo(1f, spring(dampingRatio = 0.5f, stiffness = Spring.StiffnessLow))
        }
        badgeAppear.forEachIndexed { index, anim ->
            delay(if (index == 0) 250L else 120L)
            launch {
                anim.animateTo(1f, spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow))
            }
        }
    }
    val pulse by rememberInfiniteTransition(label = "privacyPulse").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "privacyPulseProgress",
    )
    val colors = MaterialTheme.colorScheme

    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        val scale = minOf(maxWidth / DesignWidth, maxHeight / DesignHeight, 1.25f)
        Box(
            modifier = Modifier.size(DesignWidth * scale, DesignHeight * scale),
            contentAlignment = Alignment.Center,
        ) {
            val shieldSize = 128.dp * scale
            Box(
                modifier = Modifier
                    .size(shieldSize)
                    .graphicsLayer {
                        val grow = 1f + pulse * 0.45f
                        scaleX = grow * shieldAppear.value
                        scaleY = grow * shieldAppear.value
                        alpha = (1f - pulse) * 0.5f * shieldAppear.value.coerceIn(0f, 1f)
                    }
                    .clip(CircleShape)
                    .background(colors.primary.copy(alpha = 0.35f)),
            )
            Box(
                modifier = Modifier
                    .size(shieldSize)
                    .graphicsLayer {
                        scaleX = shieldAppear.value
                        scaleY = shieldAppear.value
                    }
                    .clip(CircleShape)
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.Shield,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp * scale),
                    tint = colors.onPrimaryContainer,
                )
            }
            Badges.forEachIndexed { index, badge ->
                val progress = badgeAppear[index].value
                Column(
                    modifier = Modifier
                        .offset(x = 104.dp * scale * badge.xRatio, y = 98.dp * scale * badge.yRatio)
                        .width(96.dp * scale)
                        .graphicsLayer {
                            scaleX = progress
                            scaleY = progress
                            alpha = progress.coerceIn(0f, 1f)
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp * scale),
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp * scale)
                            .clip(CircleShape)
                            .background(colors.secondaryContainer),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = badge.icon,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp * scale),
                            tint = colors.onSecondaryContainer,
                        )
                    }
                    Text(
                        text = stringResource(badge.labelRes),
                        style = MaterialTheme.typography.labelMedium,
                        color = colors.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
