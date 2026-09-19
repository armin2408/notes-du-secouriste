package com.notesdusecouriste.feature.onboarding.content

/**
 * Catalogue embarqué des mises à jour (trié versionCode décroissant à l’affichage).
 * Ajouter une entrée à chaque release user-visible.
 */
data class ChangelogEntry(
    val versionCode: Int,
    val versionName: String,
    val title: String,
    /** Markdown léger : titres, listes, gras. */
    val bodyMarkdown: String,
)

object ChangelogCatalog {
    val entries: List<ChangelogEntry> = listOf(
        ChangelogEntry(
            versionCode = 6,
            versionName = "0.3.0-beta",
            title = "Accueil, journal et identité visuelle",
            bodyMarkdown = """
                ## Nouveautés
                - Écran d’accueil (3 slides) au premier lancement
                - Journal des mises à jour accessible depuis les réglages
                - Synthèse d’intervention : aperçu PDF (zoom, paysage, partage)

                ## Améliorations
                - Nouvelle palette terrain (bleu #1565C0, sans violet)
                - Couleurs métier (normal / à surveiller / alerte / critique) plus lisibles
                - Thème clair et sombre alignés Material 3 Expressive
            """.trimIndent(),
        ),
        ChangelogEntry(
            versionCode = 5,
            versionName = "0.2.1-beta",
            title = "Photos, profil et synthèse PDF",
            bodyMarkdown = """
                ## Nouveautés
                - Photos d’intervention stockées **localement** sur l’appareil
                - Profil secouriste (identité, contact) pour les exports PDF
                - Synthèse PDF avec aperçu, zoom et orientation paysage

                ## Améliorations
                - Parcours notes simplifié (accès direct à la synthèse)
            """.trimIndent(),
        ),
        ChangelogEntry(
            versionCode = 3,
            versionName = "0.2.0-beta",
            title = "Bêta Play Store",
            bodyMarkdown = """
                - Première version proposée en test fermé Play Store
                - Notes d’intervention, mesures et questionnaires
                - Aide-mémoire intégré
            """.trimIndent(),
        ),
        ChangelogEntry(
            versionCode = 1,
            versionName = "0.1.0",
            title = "Première version",
            bodyMarkdown = """
                - Carnet de notes local pour secouristes PSE
                - Sans compte à créer
            """.trimIndent(),
        ),
    )

    fun sortedDescending(): List<ChangelogEntry> =
        entries.sortedByDescending { it.versionCode }
}
