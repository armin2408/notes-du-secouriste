---
project_name: NOTES DU SECOURISTE
updated: 2026-07-01
version: 0.2.1-beta (versionCode 5)
source_of_truth: android/ (Kotlin, Compose, Room, Hilt)
maintainer_note: Mettre à jour ce fichier quand une story est livrée ou qu’un écart spec/code est tranché.
---

# État d’implémentation — aligné sur le code

Ce document reflète **ce qui est réellement dans le dépôt** au 2026-07-01 (`0.2.1-beta`), pas uniquement le plan initial (`epics.md` / PRD).

## Synthèse par epic

| Epic | Intitulé | Avancement | Commentaire code |
|------|----------|------------|------------------|
| **1** | Fondation & accueil | **~95 %** | Accueil hub, création brouillon, thème, réglages, edge-to-edge + flou nav (Haze) ; pas de libellé « Hors ligne » (D-032) |
| **2** | Saisie notes (blocs) | **~85 %** | Victime + Mesures + Questionnaires + Commentaire, autosave ~400 ms ; blocs PRD Contexte/Gestes/Évolution/Transmission absents |
| **3** | Relevés vitaux & alertes | **~55 %** | Relevés multiples JSON + UI terrain (Glasgow, T°, glycémie) ; **pas** d’alertes ni plages de référence |
| **4** | Transmission orale | **~55 %** | **Récap** tableau lecture seule ; Script / export PDF intervention encore à faire |
| **5** | Cycle de vie | **~30 %** | Liste accueil, suppression ; **pas** de clôture ni lecture seule |
| **6** | Aide mémoire | **~90 %** | Onglets, éditeur rich text, recherche, images, PDF externe ; pas de viewer PDF interne |
| **7** | Suppression | **~60 %** | Suppression simple / multi sur l’accueil ; pas de purge auto (hors scope) |

## Écarts notables spec ↔ code

| Sujet | Plan (PRD / epics / architecture) | Code actuel |
|-------|-----------------------------------|-------------|
| Material 3 | Classique `MaterialTheme` (NFR-8) | **`MaterialExpressiveTheme`** (`core-ui/.../Theme.kt`) |
| Schéma notes | `schemaVersion` v1, blocs PRD complets | **`v2`**, JSON `InterventionNoteContent` (Victime, Mesures, Questionnaires, Commentaire) |
| Relevés vitaux | Table `VitalReading` + `recordedAt` | **`MesureEntry`** dans JSON `sectionsJson` (`InterventionNotesEntity`) |
| Accueil | Pas d’écran Historique dédié, pas « Hors ligne » | Liste sur accueil ; **pas** de badge offline |
| Epic 2 « hors constantes » | Mesures = Epic 3 | **Bloc MESURES déjà riche** (Epic 2+3 fusionnés en pratique) |
| Aide-mémoire | Markdown viewer | Éditeur **rich text** (persistance Markdown) + images + recherche |

---

## Epic 1 — Fondation app & accueil

| Story | Statut | Preuve / fichiers |
|-------|--------|-------------------|
| 1.1 Projet Android modulaire | ✅ Fait | `android/`, modules `:app`, `:core-data`, `:core-ui`, `:feature-intervention-notes`, `:feature-aide-memoire` |
| 1.2 Thème terrain & pas « bilan » | ✅ Fait (écart M3) | `NotesDuSecouristeTheme` Expressive ; strings sans « bilan » |
| 1.3 Accueil & navigation | ✅ Fait | `HomeScreen.kt`, `AppNavHost.kt` — CTA, liste, aide-mémoire, toggle thème, icône Récap |
| 1.4 Nouvelle intervention | ✅ Fait | `InterventionRepository.createDraftIntervention()` → navigation notes |
| 1.5 Thème clair/sombre & politique | ✅ Fait | `SettingsScreen.kt`, `ThemePreferencesRepository.kt`, `AppThemeViewModel.kt` |
| Edge-to-edge + flou nav 3 boutons | ✅ Fait | `EdgeToEdgeRoot.kt`, Haze, `SystemBarsAppearance.kt` |

**Non fait / partiel :** indicateur « Hors ligne » (retiré D-032) ; écran Historique séparé (fusionné dans l’accueil).

---

## Epic 2 — Saisie notes secouriste

| Story | Statut | Preuve / fichiers |
|-------|--------|-------------------|
| 2.1 Schéma & persistance auto | ✅ Fait | `InterventionNotesViewModel.scheduleSave()` — **delay 400 ms** → `repository.saveNoteContent()` ; `NotesSectionsCodec` ; `schemaVersion = "v2"` |
| 2.2 UI blocs scrollables | ✅ Fait | `InterventionNotesListContent.kt`, sticky headers ; bouton **Récap en fin de liste** (`recap_action`) |
| 2.3 Reprise après kill app | ✅ Données | Room + `observeNoteContent` ; onglet mesure par `id` ; position scroll non garantie |
| 2.4 Ergonomie terrain | 🟡 Partiel | Champs ~56 dp min, thème sombre ; **pas** d’audit WCAG AA formalisé |

### Blocs présents dans `InterventionNoteContent`

| Bloc PRD | Dans le code |
|----------|--------------|
| Victime | ✅ `VictimeBlock` |
| Mesures / constantes | ✅ Voir Epic 3 |
| Questionnaires SAMPLE / OPQRST | ✅ `QuestionnairesBlock` |
| Commentaire | ✅ `commentaire: String` |
| Contexte, Gestes, Évolution, Transmission | ❌ Absents du modèle |

### UI notes — livré

- En-tête dynamique : NOM Prénom - Âge
- Indicateur sauvegarde : « Enregistrement… » / « Enregistré »
- Validation date `dd/MM/yyyy` non bloquante
- Sticky headers empilés
- Bouton Récap en bas du scroll (plus de barre sticky Script/Clôturer)

---

## Epic 3 — Relevés vitaux, alertes & références

| Story | Statut | Preuve / fichiers |
|-------|--------|-------------------|
| 3.1 Relevés multiples par horodatage | ✅ Fait | `MesureEntry`, onglets, `addMesureEntry()` |
| 3.2 TA sys + dia | ✅ Fait | `tensionSys` / `tensionDia` |
| 3.3 Antidatage date/heure | ✅ Fait | Appui long onglet → dialogs |
| 3.4 Alertes couleur | ❌ À faire | — |
| 3.5 ⓘ plages de référence | ❌ À faire | — |
| 3.6 Réglages plages | ❌ À faire | — |

### Saisie mesures — détail

- **Respiration :** fréquence, amplitude/régularité/aspect, SpO₂
- **Circulation :** FC, amplitude/régularité/aspect, TA SYS/DIA, TRC
- **Conscience :** conscience, orientations, propos, **Glasgow** (score + interprétation)
- **Suspicion AVC :** visage, pupilles, motricité, parole, heure symptômes
- **Autres :** température, glycémie (+ unité)
- **Listes de choix :** pills connectées M3 Expressive

**Architecture :** pas de table `vital_readings` ; tout dans `sectionsJson`.

---

## Epic 4 — Transmission orale

| Story | Statut | Preuve |
|-------|--------|--------|
| 4.1 Récap | ✅ Fait | `InterventionRecapScreen`, `InterventionRecapBuilder` — tableau multi-colonnes, questionnaires ; routes notes + accueil |
| 4.2 Script rule-based | ❌ | Absent de l’UI (strings legacy possibles) |
| 4.3 Disclaimer script | ❌ | — |
| Export PDF synthèse intervention | ❌ | Roadmap (après profil + gardrails) |

---

## Epic 5 — Historique & cycle de vie

| Story | Statut | Preuve |
|-------|--------|--------|
| Liste interventions | ✅ | `HomeScreen` + `InterventionCard` |
| Statut brouillon | ✅ | `InterventionStatus.DRAFT` (enum `CLOSED` sans flux UI) |
| Clôture → lecture seule | ❌ | — |
| FR-13 duplication | — | Retiré du PRD |

---

## Epic 6 — Aide mémoire

| Story | Statut | Preuve |
|-------|--------|--------|
| Onglets + personnalisation | ✅ | Création / duplication / masquage / réordonner |
| Éditeur rich text + Markdown | ✅ | `AideMemoireRichTextEditor.kt` — gras, italique, souligné, H1/H2, listes, liens |
| Recherche | ✅ | Multi-occurrences, surlignage (viewer), navigation |
| Images | ✅ | Insertion galerie (`appimg://`) |
| Autosave | ✅ | Debounce ~400 ms |
| Ouverture PDF | ✅ (externe) | Intent + FileProvider ; pas de viewer interne |

---

## Epic 7 — Suppression

| Story | Statut | Preuve |
|-------|--------|--------|
| Suppression manuelle | ✅ | Simple + multi-sélection |
| Pas de purge auto | ✅ | Aucune purge planifiée |

---

## Prochaines priorités suggérées

Ordre produit retenu (Mary / party mode, juil. 2026) :

1. **Gardrails** — ✅ textes RGPD / disclaimer partagés + dialog 1ʳᵉ ouverture
2. **Profil secouriste** — ✅ Settings (identité, contact, compétences) via DataStore
3. **Export PDF** — ✅ synthèse type Récap + profil + disclaimer + aperçu + partage
4. **Photos dans une note** — ✅ caméra / galerie, ack RGPD **par intervention**, fichiers locaux + Room + **inclusion dans le PDF**
5. Epic 3.4–3.6 — alertes + plages
6. Epic 5 — clôture + lecture seule
7. Epic 4.2 — Script de transmission

---

## Build & test device

```powershell
cd android
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew installDebug
```

Checklist manuelle : `docs/manual-test-checklist.md`
