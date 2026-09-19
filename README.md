# Notes du Secouriste

Application Android **hors ligne** pour structurer les notes de terrain en secourisme (PSE) : victime, mesures horodatées, questionnaires, récap de transmission et aide-mémoire intégrée.

> **Version bêta** — `0.3.0-beta` (test fermé Google Play). L’application est un outil d’aide à la prise de notes ; elle ne remplace pas un document médical réglementé ni l’avis d’un professionnel de santé.

## Fonctionnalités

- **Interventions** — création, liste, sélection multiple, suppression
- **Victime** — identité, date de naissance, âge, coordonnées
- **Mesures** — relevés horodatés (respiration, circulation, conscience / Glasgow, suspicion d’AVC, température, glycémie)
- **Questionnaires** — SAMPLE, OPQRST
- **Photos** — pièces jointes locales sur l’intervention
- **Profil secouriste** — identité / contact pour les exports
- **Synthèse PDF** — aperçu, zoom, orientation paysage, partage
- **Aide-mémoire** — onglets personnalisables, éditeur rich text (gras, italique, listes, liens), images, recherche, sauvegarde automatique
- **Onboarding** — carrousel de bienvenue + journal des mises à jour
- **Thème** — clair / sombre (palette terrain), edge-to-edge avec flou de la barre de navigation (3 boutons)
- **100 % hors ligne** — aucune collecte de données par l’éditeur ; stockage local sur l’appareil

### Pas encore dans la bêta

Script de transmission · clôture d’intervention · alertes vitales

## Captures d’écran

_À compléter lors de la publication Play Store._

## Prérequis

- Android **8.0+** (API 26)
- [Android Studio](https://developer.android.com/studio) (JDK 17) pour compiler le projet

## Démarrage rapide

Le code de l’application se trouve dans le dossier [`android/`](android/).

```powershell
cd android
.\gradlew installDebug
```

Guide détaillé (USB, structure des modules, build release) : [`android/README.md`](android/README.md).

## Structure du dépôt

```
├── android/          # Projet Gradle (app + modules feature / core)
├── docs/             # Fiche Play Store, politique de confidentialité (GitHub Pages)
└── README.md         # Ce fichier
```

| Module | Rôle |
|--------|------|
| `:app` | Navigation, accueil, réglages |
| `:core:core-data` | Room, repositories, modèle de données |
| `:core:core-ui` | Thème Material 3, barre système, previews |
| `:feature:feature-intervention-notes` | Saisie notes, mesures, récap |
| `:feature:feature-aide-memoire` | Aide-mémoire |

## Stack technique

- Kotlin, Jetpack Compose, Material 3 Expressive
- Room, Hilt, Navigation Compose, DataStore
- Min SDK 26 · Target SDK 35

## Publication (bêta Play Store)

- Guide : [`docs/play-store-beta.md`](docs/play-store-beta.md)
- Textes fiche store : [`docs/store-listing-fr.md`](docs/store-listing-fr.md)
- [Politique de confidentialité](https://armin2408.github.io/notes-du-secouriste/) (GitHub Pages)

Build release signé :

```powershell
cd android
.\gradlew bundleRelease
```

AAB : `android/app/build/outputs/bundle/release/app-release.aab`

## Confidentialité

Les données saisies (notes d’intervention, aide-mémoire) restent **sur l’appareil**. Aucune transmission vers un serveur de l’éditeur. Détails : [`docs/privacy-policy-fr.md`](docs/privacy-policy-fr.md).

## Contribution

Ce dépôt est en phase de **bêta fermée**. Les retours des testeurs sont les bienvenus (bugs, ergonomie terrain).

---

_Développé pour les secouristes sur le terrain._
