package com.notesdusecouriste.feature.onboarding.content

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector
import com.notesdusecouriste.feature.onboarding.R

data class WelcomeSlide(
    val titleRes: Int,
    val bodyRes: Int,
    val icon: ImageVector,
)

object WelcomeSlides {
    val all: List<WelcomeSlide> = listOf(
        WelcomeSlide(
            titleRes = R.string.welcome_slide1_title,
            bodyRes = R.string.welcome_slide1_body,
            icon = Icons.Outlined.EditNote,
        ),
        WelcomeSlide(
            titleRes = R.string.welcome_slide2_title,
            bodyRes = R.string.welcome_slide2_body,
            icon = Icons.Outlined.PhotoCamera,
        ),
        WelcomeSlide(
            titleRes = R.string.welcome_slide3_title,
            bodyRes = R.string.welcome_slide3_body,
            icon = Icons.Outlined.Lock,
        ),
    )
}
