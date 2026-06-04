package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.annotation.StringRes
import com.notesdusecouriste.feature.interventionnotes.R

data class GlasgowOption(
    /** Clé = score numérique (ex. « 4 »). */
    val key: String,
    @StringRes val labelRes: Int,
    val score: Int,
)

object GlasgowChoices {
    val ouvertureYeux = listOf(
        GlasgowOption("4", R.string.glasgow_eye_4, 4),
        GlasgowOption("3", R.string.glasgow_eye_3, 3),
        GlasgowOption("2", R.string.glasgow_eye_2, 2),
        GlasgowOption("1", R.string.glasgow_eye_1, 1),
    )
    val reponseVerbale = listOf(
        GlasgowOption("5", R.string.glasgow_verbal_5, 5),
        GlasgowOption("4", R.string.glasgow_verbal_4, 4),
        GlasgowOption("3", R.string.glasgow_verbal_3, 3),
        GlasgowOption("2", R.string.glasgow_verbal_2, 2),
        GlasgowOption("1", R.string.glasgow_verbal_1, 1),
    )
    val reponseMotrice = listOf(
        GlasgowOption("6", R.string.glasgow_motor_6, 6),
        GlasgowOption("5", R.string.glasgow_motor_5, 5),
        GlasgowOption("4", R.string.glasgow_motor_4, 4),
        GlasgowOption("3", R.string.glasgow_motor_3, 3),
        GlasgowOption("2", R.string.glasgow_motor_2, 2),
        GlasgowOption("1", R.string.glasgow_motor_1, 1),
    )

    fun optionFor(key: String, options: List<GlasgowOption>): GlasgowOption? =
        options.find { it.key == key.trim() }
}
