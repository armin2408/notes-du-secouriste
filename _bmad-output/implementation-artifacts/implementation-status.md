---
project_name: NOTES DU SECOURISTE
updated: 2026-05-20
source_of_truth: android/ (Kotlin, Compose, Room, Hilt)
maintainer_note: Mettre à jour ce fichier quand une story est livrée ou qu’un écart spec/code est tranché.
---

# État d’implémentation — aligné sur le code

Ce document reflète **ce qui est réellement dans le dépôt** au 2026-05-20, pas uniquement le plan initial (`epics.md` / PRD).

## Synthèse par epic

| Epic | Intitulé | Avancement | Commentaire code |
|------|----------|------------|------------------|
| **1** | Fondation & accueil | **~90 %** | Accueil hub, création brouillon, thème, réglages ; pas de libellé « Hors ligne » (D-032) |
| **2** | Saisie notes (blocs) | **~70 %** | Victime + Mesures + Commentaire, autosave ~400 ms ; blocs PRD Contexte/Gestes/Évolution/Transmission absents |
| **3** | Relevés vitaux & alertes | **~45 %** | Relevés multiples JSON + UI terrain ; **pas** d’entité `VitalReading`, **pas** d’alertes ni plages de référence |
| **4** | Transmission orale | **~35 %** | **Récap** lecture seule (S-03) ; Script / export encore à faire |
| **5** | Cycle de vie | **~25 %** | Liste accueil, suppression ; **pas** de clôture ni lecture seule |
| **6** | Aide mémoire | **~5 %** | Écran placeholder uniquement |
| **7** | Suppression | **~60 %** | Suppression simple / multi sur l’accueil ; pas de purge auto (hors scope) |

## Écarts notables spec ↔ code

| Sujet | Plan (PRD / epics / architecture) | Code actuel |
|-------|-----------------------------------|-------------|
| Material 3 | Classique `MaterialTheme` (NFR-8) | **`MaterialExpressiveTheme`** (`core-ui/.../Theme.kt`) |
| Schéma notes | `schemaVersion` v1, blocs PRD complets | **`v2`**, JSON `InterventionNoteContent` (Victime, Mesures, Commentaire) |
| Relevés vitaux | Table `VitalReading` + `recordedAt` | **`MesureEntry`** dans JSON `sectionsJson` (`InterventionNotesEntity`) |
| Accueil | Pas d’écran Historique dédié, pas « Hors ligne » | Liste sur accueil ; **pas** de badge offline |
| Epic 2 « hors constantes » | Mesures = Epic 3 | **Bloc MESURES déjà riche** (Epic 2+3 fusionnés en pratique) |

---

## Epic 1 — Fondation app & accueil

| Story | Statut | Preuve / fichiers |
|-------|--------|-------------------|
| 1.1 Projet Android modulaire | ✅ Fait | `android/`, modules `:app`, `:core-data`, `:core-ui`, `:feature-intervention-notes`, `:feature-aide-memoire` |
| 1.2 Thème terrain & pas « bilan » | ✅ Fait (écart M3) | `NotesDuSecouristeTheme` Expressive ; strings sans « bilan » |
| 1.3 Accueil & navigation | ✅ Fait | `HomeScreen.kt`, `AppNavHost.kt` — CTA, liste, icône aide mémoire, toggle thème |
| 1.4 Nouvelle intervention | ✅ Fait | `InterventionRepository.createDraftIntervention()` → navigation notes |
| 1.5 Thème clair/sombre & politique | ✅ Fait | `SettingsScreen.kt`, `ThemePreferencesRepository.kt`, `AppThemeViewModel.kt` |

**Non fait / partiel :** indicateur « Hors ligne » (retiré D-032) ; écran Historique séparé (fusionné dans l’accueil).

---

## Epic 2 — Saisie notes secouriste

| Story | Statut | Preuve / fichiers |
|-------|--------|-------------------|
| 2.1 Schéma & persistance auto | ✅ Partiel | `InterventionNotesViewModel.scheduleSave()` — **delay 400 ms** → `repository.saveNoteContent()` ; `NotesSectionsCodec` ; `schemaVersion = "v2"` |
| 2.2 UI blocs scrollables | ✅ Partiel | `InterventionNotesListContent.kt`, `NoteSectionCard`, en-têtes sticky (`StackedStickyHeaders.kt`) ; barre basse Récap/Script/Clôturer = **placeholders** |
| 2.3 Reprise après kill app | ✅ Données | Room + `observeNoteContent` ; **scroll/onglet** : reprise onglet mesure par `id`, pas garanti même position scroll |
| 2.4 Ergonomie terrain | 🟡 Partiel | Champs ~56 dp min, thème sombre ; **pas** d’audit WCAG AA formalisé |

### Blocs présents dans `InterventionNoteContent`

| Bloc PRD | Dans le code |
|----------|--------------|
| Victime (nom, prénom, date naissance, âge, coordonnées) | ✅ `VictimeBlock` + formatage (`VictimeFormatting.kt`, `DateNaissanceUtils.kt`) |
| Mesures / constantes | ✅ Voir Epic 3 (implémenté dans le même écran) |
| Commentaire | ✅ `commentaire: String` |
| Contexte, Gestes, Évolution, Transmission | ❌ Absents du modèle |

### UI notes — fonctionnalités livrées au-delà des stories 2.x

- En-tête dynamique (`formatNoteHeader`) : NOM Prénom - Âge
- Indicateur sauvegarde : « Enregistrement… » / « Enregistré » (`InterventionNotesScreen`)
- Validation date `dd/MM/yyyy` non bloquante + icône erreur (`FrenchDateValidation.kt`, `BoldLabelField`)
- Sticky headers empilés (bloc Mesures + onglets + sous-sections Respiration, etc.)

---

## Epic 3 — Relevés vitaux, alertes & références

| Story | Statut | Preuve / fichiers |
|-------|--------|-------------------|
| 3.1 Relevés multiples par horodatage | ✅ Fait (modèle JSON) | `MesureEntry`, onglets `MesureTabsRow.kt`, `addMesureEntry()` |
| 3.2 TA sys + dia | ✅ Fait | `CirculationMesure.tensionSys` / `tensionDia`, `TensionArterielleFields.kt`, migration legacy `MesuresBlockMigration.kt` |
| 3.3 Antidatage date/heure | ✅ Fait | Appui long onglet → `MesureTabActionsSheet` → `MesureDateTimeDialog` ; affichage `HH:mm` ou `dd/MM HH:mm` |
| 3.4 Alertes couleur | ❌ À faire | Pas de `VitalAlertEvaluator` ni plages |
| 3.5 ⓘ plages de référence | ❌ À faire | — |
| 3.6 Réglages plages | ❌ À faire | — |

### Saisie mesures — détail implémenté

- **Respiration :** fréquence (3 chiffres + `bpm`), amplitude/régularité/aspect (listes), SpO₂ (3 chiffres + `%`)
- **Circulation :** FC, amplitude/régularité/aspect, TA SYS/DIA, TRC
- **Conscience :** conscience, orientations, propos
- **Suspicion AVC :** visage, pupilles, motricité, parole, heure symptômes
- **Listes de choix :** `ChoiceChipGroup` — pills connectées M3 Expressive, vert = 1ère option, jaune = autres
- **Heure relevé :** saisie filtrée `HH:mm`, `:` auto (`FrenchTimeValidation.kt`, `TimeVisualTransformation.kt`)

**Architecture :** pas encore de table `vital_readings` ; tout est dans `sectionsJson` (décision future : voir `architecture.md` § migration).

---

## Epic 4 — Transmission orale

| Story | Statut | Preuve |
|-------|--------|--------|
| 4.1 Récap | ✅ Partiel | `InterventionRecapScreen`, `InterventionRecapBuilder`, route `intervention/{id}/recap` ; typo 22 sp ; ordre Victime → Mesures (relevés HH:mm, TA sys/dia) → Commentaire |
| 4.2 Script rule-based | ❌ | Bouton Script → snackbar `action_coming_soon` |
| 4.3 Disclaimer script | ❌ | — |

---

## Epic 5 — Historique & cycle de vie

| Story | Statut | Preuve |
|-------|--------|--------|
| Liste interventions | ✅ | `HomeScreen` + `InterventionCard` |
| Statut brouillon | ✅ | `InterventionStatus.DRAFT` |
| Clôture → lecture seule | ❌ | Bouton Clôturer = placeholder |
| FR-13 duplication | — | Retiré du PRD |

---

## Epic 6 — Aide mémoire

| Story | Statut |
|-------|--------|
| 6.x onglets Markdown + personnalisation | ✅ | Onglets (création/duplication/masquage/réordonner) + contenu offline (`feature-aide-memoire/.../AideMemoirePlaceholderScreen.kt`) |
| 6.x ouverture PDF | ✅ (externe) | Les PDFs s’ouvrent via l’app PDF du téléphone (Intent + FileProvider) ; pas de viewer PDF interne |

---

## Epic 7 — Suppression

| Story | Statut | Preuve |
|-------|--------|--------|
| Suppression manuelle | ✅ | `HomeViewModel` — suppression simple + mode sélection multi |
| Pas de purge auto | ✅ | Aucune purge planifiée |

---

## Prochaines priorités suggérées (ordre technique)

1. **Epic 2 — compléter les blocs PRD manquants** ou trancher le périmètre v1 (Contexte, Gestes, etc.)
2. **Epic 3.4–3.6** — alertes + plages de référence (spike schéma si extraction JSON → entités)
3. **Epic 4** — écran Récap lecture seule (données déjà en Room)
4. **Epic 5** — clôture + mode lecture seule
5. **Epic 6** — aide mémoire embarquée

---

## Build & test device

```powershell
cd android
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew installDebug
```

Checklist manuelle : `docs/manual-test-checklist.md`
