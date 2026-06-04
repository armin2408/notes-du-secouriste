# NOTES DU SECOURISTE — Application Android

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

L’app **NOTES DU SECOURISTE** s’installe et se lance.

## Parcours testable (état code 2026-05-20)

### Accueil (Epic 1)

- Titre **NOTES DU SECOURISTE**, **Nouvelle intervention** (sous-titre Notes secouriste)
- Liste des interventions sur l’accueil (pas d’écran Historique séparé)
- Icône **Aide mémoire** (placeholder), **toggle thème** clair/sombre
- **Réglages** : politique thème (système / dernier choisi)
- Suppression : appui long → mode sélection → supprimer une ou plusieurs interventions
- Mode avion : création et liste OK

### Écran notes (Epics 2 + 3 partiels)

- **Autosave** ~400 ms après chaque modification (indicateur Enregistrement… / Enregistré)
- Blocs : **VICTIME**, **MESURES**, **COMMENTAIRE** (JSON Room `sectionsJson`, `schemaVersion` v2)
- Victime : nom, prénom formaté, date de naissance `jj/mm/aaaa` + âge auto, coordonnées
- Mesures : plusieurs relevés horodatés (onglets), sous-blocs Respiration / Circulation / Conscience / Suspicion AVC
- TA **SYS** + **DIA**, fréquences avec suffixes `bpm` / `%`, listes de choix (pills connectées)
- Appui long sur un onglet heure : modifier date, heure, supprimer le relevé
- Validation date/heure non bloquante (bordure + icône si format invalide)
- En-têtes **sticky** empilés (Mesures + onglets + sous-sections)
- **Récap** : écran lecture seule (Victime → Mesures par relevé → Commentaire), typo 22 sp
- Barre basse : **Script / Clôturer** → « Bientôt disponible » (Epic 4–5 à venir)

### Non implémenté

- Alertes hors plage, plages de référence ⓘ, réglages plages (Epic 3)
- Récap, script transmission, clôture lecture seule (Epics 4–5)
- Aide mémoire Markdown (Epic 6)
- Blocs PRD Contexte, Gestes, Évolution, Transmission (hors modèle actuel)

Documentation détaillée : [`../_bmad-output/implementation-artifacts/implementation-status.md`](../_bmad-output/implementation-artifacts/implementation-status.md)

## Structure modules

| Module | Rôle |
|--------|------|
| `:app` | Navigation, accueil, réglages thème |
| `:core:core-data` | Room, repositories, modèle `InterventionNoteContent`, validations date/heure |
| `:core:core-ui` | Thème Material 3 **Expressive**, couleurs terrain |
| `:feature:feature-intervention-notes` | Écran saisie notes, mesures, sticky headers |
| `:feature:feature-aide-memoire` | Placeholder aide mémoire |

## Commandes utiles

```powershell
.\gradlew assembleDebug    # APK debug
.\gradlew test               # tests unitaires
adb logcat -s NotesDuSecouriste  # logs (après tag ajouté)
```

APK généré : `app/build/outputs/apk/debug/app-debug.apk`

## Publication Play Store (bêta)

Guide complet : [`../docs/play-store-beta.md`](../docs/play-store-beta.md)

Build release signé (après `keystore.properties` + keystore) :

```powershell
cd android
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat bundleRelease
```

AAB : `app/build/outputs/bundle/release/app-release.aab`
