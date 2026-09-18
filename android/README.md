# NOTES DU SECOURISTE — Application Android

**Version :** `0.2.1-beta` (versionCode 5) · minSdk 26 · targetSdk 35 · compileSdk 37

## Prérequis

- [Android Studio](https://developer.android.com/studio) (Ladybug ou plus récent)
- JDK 17 (inclus avec Android Studio)
- Téléphone Android **API 26+** avec **débogage USB** activé

## Ouvrir le projet

1. Android Studio → **Open** → dossier `android/`
2. Attendre la sync Gradle (téléchargement des dépendances la première fois)
3. Si demandé : accepter les licences SDK

### `local.properties`

Android Studio le crée automatiquement. Sinon, à la racine de `android/` :

```properties
sdk.dir=C\:\\Users\\VOTRE_USER\\AppData\\Local\\Android\\Sdk
```

## Tester sur téléphone USB

1. Sur le téléphone : **Paramètres → Options développeur → Débogage USB** (activé)
2. Brancher le câble USB, accepter « Autoriser le débogage »
3. Vérifier la détection :

```powershell
adb devices
```

4. Dans Android Studio : liste des appareils → choisir votre téléphone
5. **Run** ▶ (module `app`, build variant `debug`)

Ou en ligne de commande :

```powershell
cd android
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew installDebug
```

L’app **Notes du Secouriste** s’installe et se lance.

## Parcours testable (état code 0.2.1-beta)

### Accueil (Epic 1)

- Titre **Notes du Secouriste**, badge **Bêta**, CTA **Nouvelle intervention**
- Liste des interventions (identité + date) ; icône **Récap** sur chaque ligne
- Icône **Aide-mémoire**, **toggle thème** clair/sombre
- **Réglages** : politique thème (système / dernier choisi)
- Suppression : appui long → mode sélection → supprimer une ou plusieurs interventions
- Mode avion : création et liste OK
- Edge-to-edge : barre de navigation 3 boutons transparente avec flou (**Haze**)

### Écran notes (Epics 2 + 3 + 4 partiels)

- **Autosave** ~400 ms (indicateur Enregistrement… / Enregistré)
- Blocs : **VICTIME**, **MESURES**, **QUESTIONNAIRES** (SAMPLE / OPQRST), **COMMENTAIRE**
- Persistance : JSON Room `sectionsJson`, `schemaVersion` v2
- Victime : nom, prénom formaté, date de naissance `jj/mm/aaaa` + âge auto, coordonnées
- Mesures : relevés horodatés (onglets) — Respiration, Circulation, Conscience (Glasgow), Suspicion AVC, température, glycémie
- TA **SYS** + **DIA**, fréquences avec suffixes, listes de choix (pills)
- Appui long sur un onglet heure : modifier date / heure / supprimer le relevé
- En-têtes **sticky** empilés (Mesures + onglets + sous-sections)
- Bouton **Récap** en **fin de liste** (pas de barre fixe en bas)

### Récap (Epic 4 partiel)

- Écran lecture seule depuis les notes ou l’accueil
- Synthèse type tableau (relevés multi-colonnes) + questionnaires
- **Pas encore** : Script de transmission, Clôture lecture seule, export PDF d’intervention

### Aide-mémoire (Epic 6)

- Onglets personnalisables (créer, masquer, réordonner, dupliquer)
- Éditeur **rich text** (gras, italique, souligné, titres, listes, liens)
- Insertion d’images (galerie), recherche multi-occurrences, autosave ~400 ms
- Liens PDF : ouverture via l’app PDF du téléphone (Intent + FileProvider)
- Contenu persisté en Markdown (compatible export)

### Non implémenté (roadmap)

- Alertes hors plage, plages de référence ⓘ, réglages plages (Epic 3)
- Script de transmission, clôture → lecture seule (Epics 4–5)
- Photos dans une note d’intervention
- Profil secouriste (réglages)
- Export PDF de la synthèse d’intervention
- Blocs PRD Contexte, Gestes, Évolution, Transmission (hors modèle actuel)

Documentation détaillée : [`../_bmad-output/implementation-artifacts/implementation-status.md`](../_bmad-output/implementation-artifacts/implementation-status.md)

## Structure modules

| Module | Rôle |
|--------|------|
| `:app` | Navigation, accueil, réglages thème, edge-to-edge |
| `:core:core-data` | Room, repositories, modèle `InterventionNoteContent`, validations |
| `:core:core-ui` | Thème Material 3 **Expressive**, barre système (Haze), previews |
| `:feature:feature-intervention-notes` | Saisie notes, mesures, questionnaires, récap |
| `:feature:feature-aide-memoire` | Onglets, éditeur rich text, recherche, images |

## Commandes utiles

```powershell
.\gradlew assembleDebug    # APK debug
.\gradlew bundleRelease    # AAB Play Store (keystore requis)
.\gradlew test             # tests unitaires
adb logcat -s NotesDuSecouriste  # logs (après tag ajouté)
```

APK debug : `app/build/outputs/apk/debug/app-debug.apk`  
AAB release : `app/build/outputs/bundle/release/app-release.aab`

## Publication Play Store (bêta)

Guide complet : [`../docs/play-store-beta.md`](../docs/play-store-beta.md)

```powershell
cd android
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat bundleRelease
```
