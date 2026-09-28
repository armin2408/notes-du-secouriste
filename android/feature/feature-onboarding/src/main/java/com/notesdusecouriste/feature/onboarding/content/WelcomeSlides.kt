package com.notesdusecouriste.feature.onboarding.content

import androidx.annotation.RawRes
import com.notesdusecouriste.feature.onboarding.R

sealed interface WelcomeVisual {
    data class PhoneVideo(@RawRes val videoRes: Int) : WelcomeVisual
    data object Privacy : WelcomeVisual
}

data class WelcomeSlide(
    val titleRes: Int,
    val bodyRes: Int,
    val visual: WelcomeVisual,
)

object WelcomeSlides {
    val all: List<WelcomeSlide> = listOf(
        WelcomeSlide(
            titleRes = R.string.welcome_slide1_title,
            bodyRes = R.string.welcome_slide1_body,
            visual = WelcomeVisual.PhoneVideo(R.raw.welcome_demo_notes),
        ),
        WelcomeSlide(
            titleRes = R.string.welcome_slide2_title,
            bodyRes = R.string.welcome_slide2_body,
            visual = WelcomeVisual.PhoneVideo(R.raw.welcome_demo_aide_memoire),
        ),
        WelcomeSlide(
            titleRes = R.string.welcome_slide3_title,
            bodyRes = R.string.welcome_slide3_body,
            visual = WelcomeVisual.Privacy,
        ),
    )
}
