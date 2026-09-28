package com.notesdusecouriste.feature.onboarding.ui

import androidx.annotation.RawRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min

private const val VideoAspect = 1008f / 2244f
private const val BezelRatio = 0.04f
private const val HandBelowRatio = 0.3f
private const val WidthRatio = 1.7f
private const val PhoneLeftRatio = 0.33f

private val Skin = Color(0xFFF1C7A5)
private val SkinShade = Color(0xFFD9A07A)
private val Nail = Color(0xFFF8DCC8)
private val PhoneBody = Color(0xFF1E2321)
private val PhoneEdge = Color(0xFF3A413E)

private fun phoneHeightRatio(): Float {
    val screenWidth = 1f - 2 * BezelRatio
    return screenWidth / VideoAspect + 2 * BezelRatio
}

/**
 * Téléphone tenu en main qui entre par le bas, avec une vidéo en boucle à l’écran.
 * @param isActive page visible : déclenche l’apparition et la lecture.
 */
@Composable
internal fun PhoneInHandVisual(
    @RawRes videoRes: Int,
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val appear = remember { Animatable(0f) }
    LaunchedEffect(isActive) {
        if (isActive && appear.value == 0f) {
            appear.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = 0.72f,
                    stiffness = Spring.StiffnessVeryLow,
                ),
            )
        }
    }
    val float by rememberInfiniteTransition(label = "phoneFloat").animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "phoneFloatOffset",
    )
    val fadeColor = MaterialTheme.colorScheme.background
    val sleeveColor = MaterialTheme.colorScheme.primary

    BoxWithConstraints(
        modifier = modifier.clipToBounds(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        val heightRatio = phoneHeightRatio() + HandBelowRatio
        val phoneWidth: Dp = min(min(maxHeight / heightRatio, maxWidth / WidthRatio), 240.dp)
        val phoneHeight = phoneWidth * phoneHeightRatio()
        val bezel = phoneWidth * BezelRatio
        val screenWidth = phoneWidth - bezel * 2
        val screenHeight = phoneHeight - bezel * 2
        val phoneLeft = phoneWidth * PhoneLeftRatio

        Box(
            modifier = Modifier
                .size(width = phoneWidth * WidthRatio, height = phoneWidth * heightRatio)
                .graphicsLayer {
                    val p = appear.value
                    alpha = p.coerceIn(0f, 1f)
                    translationY = (1f - p) * size.height * 0.45f + float * 3.dp.toPx() * p
                    rotationZ = -4f + (1f - p) * 14f
                    transformOrigin = TransformOrigin(0.6f, 1f)
                },
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawHandBack(unit = phoneWidth.toPx(), phoneHeight = phoneHeight.toPx(), sleeveColor)
                drawPhoneBody(
                    left = phoneLeft.toPx(),
                    width = phoneWidth.toPx(),
                    height = phoneHeight.toPx(),
                )
            }
            LoopingRawVideo(
                videoRes = videoRes,
                playing = isActive,
                modifier = Modifier
                    .offset(x = phoneLeft + bezel, y = bezel)
                    .size(width = screenWidth, height = screenHeight)
                    .clip(RoundedCornerShape(phoneWidth * 0.1f))
                    .background(Color.Black),
            )
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawHandFront(
                    unit = phoneWidth.toPx(),
                    phoneLeft = phoneLeft.toPx(),
                    phoneHeight = phoneHeight.toPx(),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(phoneWidth * 0.35f)
                .background(Brush.verticalGradient(listOf(Color.Transparent, fadeColor))),
        )
    }
}

private fun DrawScope.drawSkinRoundRect(topLeft: Offset, size: Size, radius: Float) {
    drawRoundRect(color = Skin, topLeft = topLeft, size = size, cornerRadius = CornerRadius(radius))
    drawRoundRect(
        color = SkinShade,
        topLeft = topLeft,
        size = size,
        cornerRadius = CornerRadius(radius),
        style = Stroke(width = radius * 0.12f),
    )
}

private fun DrawScope.drawHandBack(unit: Float, phoneHeight: Float, sleeveColor: Color) {
    rotate(degrees = -18f, pivot = Offset(1.2f * unit, phoneHeight - 0.1f * unit)) {
        drawRoundRect(
            color = Skin,
            topLeft = Offset(0.9f * unit, phoneHeight - 0.25f * unit),
            size = Size(0.62f * unit, 1.1f * unit),
            cornerRadius = CornerRadius(0.3f * unit),
        )
        drawRoundRect(
            color = sleeveColor,
            topLeft = Offset(0.84f * unit, phoneHeight + 0.2f * unit),
            size = Size(0.74f * unit, 0.8f * unit),
            cornerRadius = CornerRadius(0.12f * unit),
        )
    }
    drawOval(
        color = Skin,
        topLeft = Offset(0.4f * unit, phoneHeight - 0.32f * unit),
        size = Size(1.0f * unit, 0.6f * unit),
    )
}

private fun DrawScope.drawPhoneBody(left: Float, width: Float, height: Float) {
    val radius = CornerRadius(width * 0.14f)
    drawRoundRect(
        color = PhoneEdge,
        topLeft = Offset(left + width - width * 0.01f, height * 0.2f),
        size = Size(width * 0.03f, height * 0.08f),
        cornerRadius = CornerRadius(width * 0.015f),
    )
    drawRoundRect(color = PhoneBody, topLeft = Offset(left, 0f), size = Size(width, height), cornerRadius = radius)
    drawRoundRect(
        color = PhoneEdge,
        topLeft = Offset(left, 0f),
        size = Size(width, height),
        cornerRadius = radius,
        style = Stroke(width = width * 0.012f),
    )
}

private fun DrawScope.drawHandFront(unit: Float, phoneLeft: Float, phoneHeight: Float) {
    val fingerHeight = 0.12f * unit
    listOf(0.17f, 0.18f, 0.16f, 0.13f).forEachIndexed { index, length ->
        val top = phoneHeight * (0.52f + index * 0.1f)
        drawSkinRoundRect(
            topLeft = Offset(phoneLeft - length * unit + 0.06f * unit, top),
            size = Size(length * unit, fingerHeight),
            radius = fingerHeight / 2f,
        )
    }

    val thumbPivot = Offset(phoneLeft + 1.03f * unit, phoneHeight * 0.97f)
    rotate(degrees = -30f, pivot = thumbPivot) {
        val thumbWidth = 0.2f * unit
        val thumbLength = 0.46f * unit
        val topLeft = Offset(thumbPivot.x - thumbWidth / 2f, thumbPivot.y - thumbLength)
        drawSkinRoundRect(topLeft = topLeft, size = Size(thumbWidth, thumbLength), radius = thumbWidth / 2f)
        drawRoundRect(
            color = Nail,
            topLeft = Offset(topLeft.x + thumbWidth * 0.2f, topLeft.y + thumbWidth * 0.18f),
            size = Size(thumbWidth * 0.6f, thumbWidth * 0.62f),
            cornerRadius = CornerRadius(thumbWidth * 0.28f),
        )
    }
    drawOval(
        color = Skin,
        topLeft = Offset(phoneLeft + 0.72f * unit, phoneHeight - 0.14f * unit),
        size = Size(0.56f * unit, 0.42f * unit),
    )
}
