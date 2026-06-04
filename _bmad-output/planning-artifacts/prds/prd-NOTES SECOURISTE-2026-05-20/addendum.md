# Addendum — NOTES DU SECOURISTE

*Technical and UX depth referenced by the PRD but not required in the main narrative.*

## A.1 Platform & architecture hints (for `bmad-create-architecture`)

- **Stack (recommended):** Kotlin, Jetpack Compose, Room, Hilt; modular `:feature-intervention_notes`, `:feature-aide_memoire`, `:core-data`.
- **UI kit :** Material 3 **classique** (`MaterialTheme`) — **not** Material 3 Expressive / `MaterialExpressiveTheme` for MVP.
- **Offline-first:** Room as source of truth; no network dependency in MVP.
- **Data model sketch:**
  - `Intervention` — id, timestamps, status (`draft` | `closed`), schema version, `nom`, `prenom`, `age` (nullable en brouillon ; affichage liste accueil FR-11).
  - **Theme preferences (DataStore)** — `themeDefaultPolicy` (`SYSTEM` | `LAST_CHOSEN`, default `SYSTEM`) ; `lastExplicitTheme` (`LIGHT` | `DARK`). Cold start : if `LAST_CHOSEN`, ignore `isSystemInDarkTheme()`.
  - `InterventionNotes` — sections as typed JSON or normalized tables; links to `Intervention`.
  - `VitalReading` — id, interventionId, vitalType (TA, FC, …), `recordedAt` (DateTime, timezone locale), alertLevel.
    - Scalar: `value` (Double/Int).
    - TA: `systolic`, `diastolic` (two fields, same `recordedAt`).
  - UI derives display `HH:mm` from `recordedAt`; show date prefix if `recordedAt.date != intervention.startDate`.
  - `ReferenceRangeProfile` — age band, constant key, min/max, alert severity thresholds.
  - `MemoCard` — Markdown body, assets URIs, tags, sort order, `isUserEdited`.
- **Bounded contexts:** Operational intervention data ≠ aide mémoire editorial content.

## A.2 Aide mémoire — Markdown & repères

Default pack: 5–10 PSE-oriented cards. Template shape:

```markdown
# {Situation} — repères

![{schema}](assets/...)

- **Repère 1:** ...
- **Repère 2:** ...
```

- Renderer: lightweight Markdown → Compose; images from app storage.
- Edit mode: simplified toolbar (not raw syntax) for glove-off / post-intervention editing.
- **Accueil :** header (icône aide mémoire → grille situations) ; pas d'écran Historique séparé.
- Aide mémoire: grid of situation cards → detail view.

## A.3 Reference values & alerts

- Per constant (TA, FC, FR, SpO2, etc.): ranges per **tranche d'âge** (nourrisson, enfant, adulte, personne âgée — labels TBD).
- UI: field border/background color amber (limit) / red (critical); no modal blocking save.
- ⓘ opens bottom sheet with age-appropriate reference table.
- Settings screen: edit ranges; reset to factory defaults.

## A.4 Script de transmission (offline, v1)

Rule-based template engine (not LLM):

- Input: closed or in-progress `InterventionNotes` fields.
- Output: ordered short sentences for oral handoff (chef d'équipe, SAMU phone prep).
- Sections: lieu/circonstances → victime → constantes (relevés horodatés HH:mm, alert flags) → gestes → évolution.
- Disclaimer in UI: « Relire avant transmission ».

## A.5 Security & GDPR (implementation notes)

- App lock (PIN/biometric) — MVP+ if not P0.
- Encryption at rest for Room DB.
- Manual delete per intervention (liste accueil). No automatic retention purge (D-034).
- Strip EXIF on photo import if photos added later.

## A.6 Export PDF (post-MVP)

When implemented: header fixed to **« Notes secouriste — intervention »** per D-006.
