package com.notesdusecouriste.core.data.preferences

/**
 * Profil du secouriste — injecté dans le PDF à l’export (pas dupliqué dans chaque note).
 */
data class SecouristeProfile(
    val prenom: String = "",
    val nom: String = "",
    val contact: String = "",
    val organisme: String = "",
    val competences: String = "",
) {
    val isBlank: Boolean
        get() = listOf(prenom, nom, contact, organisme, competences).all { it.isBlank() }

    fun displayName(): String = listOf(prenom.trim(), nom.trim())
        .filter { it.isNotEmpty() }
        .joinToString(" ")
}
