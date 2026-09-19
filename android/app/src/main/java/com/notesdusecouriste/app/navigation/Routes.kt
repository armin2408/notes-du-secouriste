package com.notesdusecouriste.app.navigation

object Routes {
    const val HOME = "home"
    const val ONBOARDING = "onboarding?preview={preview}"
    const val CHANGELOG = "changelog"
    const val INTERVENTION = "intervention/{interventionId}"
    const val INTERVENTION_RECAP = "intervention/{interventionId}/recap"
    const val AIDE_MEMOIRE = "aide_memoire"
    const val SETTINGS = "settings"
    const val SECOURISTE_PROFILE = "settings/profile"

    fun intervention(id: Long) = "intervention/$id"

    fun interventionRecap(id: Long) = "intervention/$id/recap"

    fun onboarding(preview: Boolean = false) = "onboarding?preview=$preview"
}
