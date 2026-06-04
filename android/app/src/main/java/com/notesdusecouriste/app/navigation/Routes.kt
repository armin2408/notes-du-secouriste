package com.notesdusecouriste.app.navigation

object Routes {
    const val HOME = "home"
    const val INTERVENTION = "intervention/{interventionId}"
    const val INTERVENTION_RECAP = "intervention/{interventionId}/recap"
    const val AIDE_MEMOIRE = "aide_memoire"
    const val SETTINGS = "settings"

    fun intervention(id: Long) = "intervention/$id"

    fun interventionRecap(id: Long) = "intervention/$id/recap"
}
