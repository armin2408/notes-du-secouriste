package com.notesdusecouriste.feature.interventionnotes.ui

import androidx.annotation.StringRes
import com.notesdusecouriste.core.data.model.GlasgowMesure
import com.notesdusecouriste.feature.interventionnotes.R

fun GlasgowMesure.totalScore(): Int? {
    val eye = ouvertureYeux.trim().toIntOrNull() ?: return null
    val verbal = reponseVerbale.trim().toIntOrNull() ?: return null
    val motor = reponseMotrice.trim().toIntOrNull() ?: return null
    return eye + verbal + motor
}

fun GlasgowMesure.isComplete(): Boolean = totalScore() != null

@StringRes
fun GlasgowMesure.interpretationRes(): Int? {
    val score = totalScore() ?: return null
    return when (score) {
        15 -> R.string.glasgow_interp_15
        in 13..14 -> R.string.glasgow_interp_13_14
        in 9..12 -> R.string.glasgow_interp_9_12
        in 4..8 -> R.string.glasgow_interp_4_8
        3 -> R.string.glasgow_interp_3
        else -> R.string.glasgow_interp_unknown
    }
}
