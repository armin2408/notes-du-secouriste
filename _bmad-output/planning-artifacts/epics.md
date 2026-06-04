---
stepsCompleted: [1, 2, 3, 4]
status: final
inputDocuments:
  - prds/prd-NOTES SECOURISTE-2026-05-20/prd.md
  - prds/prd-NOTES SECOURISTE-2026-05-20/addendum.md
  - prds/prd-NOTES SECOURISTE-2026-05-20/.decision-log.md
  - ux-design-specification.md
project_name: NOTES DU SECOURISTE
author: Marti
date: 2026-05-21
architecture_note: Architecture.md non produit — exigences techniques issues de addendum.md
---

# NOTES DU SECOURISTE — Epic Breakdown

## Overview

Décomposition PRD v1 (FR-1 à FR-17), spec UX et addendum technique en **7 epics** orientés valeur utilisateur, stories ordonnées sans dépendance future, critères d'acceptation testables.

**Hors scope stories (plan initial) :** export PDF, sync cloud, LLM, iOS, Material 3 Expressive.

> **État code (2026-05-20) :** le développement Android a dépassé ce que ce document laissait entendre pour les Epics 1–3. Voir le fichier à jour : [`../implementation-artifacts/implementation-status.md`](../implementation-artifacts/implementation-status.md). Résumé : Epic 1 ~90 %, Epic 2 ~70 % (3 blocs sur 7+ PRD), Epic 3 ~45 % (relevés UI oui, alertes non), Epic 4–6 largement à faire. **M3 Expressive** et **`schemaVersion` v2** sont en production dans le code.

---

## Requirements Inventory

### Functional Requirements

```
FR-1: Créer une intervention (brouillon, horodatage, offline, accès saisie immédiat)
FR-2: Aucun libellé « bilan » / « fiche bilan » dans l'UI v1
FR-3: Formulaire notes secouriste unifié PSE1/PSE2 (schemaVersion v1)
FR-4: Saisie par blocs, sauvegarde automatique, reprise après interruption
FR-5: Ergonomie terrain (≥48dp, WCAG AA, thème sombre)
FR-6: Relevés multiples par constante ; recordedAt date+heure ; affichage HH:mm ; antidatage ; TA sys+dia
FR-7: Alertes non bloquantes par relevé (limite / critique)
FR-8: Valeurs de référence personnalisables par constante et tranche d'âge + ⓘ
FR-9: Récap écran lisible pour transmission orale (pas d'export fichier)
FR-10: Script de transmission offline rule-based
FR-11: Liste interventions sur accueil (NOM Prénom - Âge, date-heure, statut)
FR-18: Thème clair/sombre + politique ouverture (Système | Dernier choisi)
FR-12: Clôture → lecture seule
~~FR-13: Dupliquer intervention clôturée~~ (retiré D-033)
FR-14: Aide mémoire — cartes situation → repères Markdown
FR-15: Pack aide mémoire par défaut embarqué (5–10 fiches)
FR-16: Édition / personnalisation aide mémoire sur téléphone
FR-17: Suppression manuelle uniquement (pas de purge auto — D-034)
```

### NonFunctional Requirements

```
NFR-1: Persistance champ < 500 ms
NFR-2: Aide mémoire carte < 2 s
NFR-3: 100 % fonctionnel en mode avion (cœur métier)
NFR-4: Données santé — minimisation, pas de télémétrie invasive MVP
NFR-5: Chiffrement au repos recommandé (addendum)
NFR-6: WCAG AA parcours critique + TalkBack ordre = dictée
NFR-7: Android API min 26+ ; Kotlin + Compose + Room + Hilt
NFR-8: Material 3 classique (MaterialTheme), pas Expressive
```

### Additional Requirements

```
- Modules : :app, :feature-intervention_notes, :feature-aide_memoire, :core-data, :core-ui
- Room source of truth, offline-first
- Entités : Intervention, InterventionNotes (sections), VitalReading, ReferenceRangeProfile, MemoCard
- VitalReading : recordedAt DateTime ; TA = systolic + diastolic
- Pas d'architecture.md — valider schéma blocs quand fiches papier disponibles (O-003)
```

### UX Design Requirements

```
UX-DR1: Accueil hub — header (icône Aide mémoire + toggle thème) + CTA « Nouvelle intervention » au-dessus + liste interventions en dessous
UX-DR2: Thème D1 Terrain nocturne (tokens couleur alerte, statut brouillon/clôturée)
UX-DR3: Material 3 classique MaterialTheme
UX-DR4: Composant VitalSignGroup + VitalReadingRow + DateTimeEditSheet
UX-DR5: TA saisie consécutive [sys] / [dia] sur une ligne
UX-DR6: Affichage HH:mm ; dd/MM HH:mm si date ≠ jour intervention
UX-DR7: InterventionBlockCard pour blocs non-vitaux
UX-DR8: SituationCard grille aide mémoire + RepereMarkdownView
UX-DR9: TransmissionScriptView + disclaimer
UX-DR10: StatusBadge Brouillon / Clôturée
UX-DR11: ~~OfflineIndicator~~ retiré (D-032)
UX-DR13: Thème clair/sombre + DataStore (SYSTEM | LAST_CHOSEN)
UX-DR12: Écrans S-01 à S-11 selon inventaire UX
```

### FR Coverage Map

| FR | Epic |
|----|------|
| FR-1 | Epic 1 |
| FR-2 | Epic 1 |
| FR-3 | Epic 2 |
| FR-4 | Epic 2 |
| FR-5 | Epic 2 |
| FR-6 | Epic 3 |
| FR-7 | Epic 3 |
| FR-8 | Epic 3 |
| FR-9 | Epic 4 |
| FR-10 | Epic 4 |
| FR-11 | Epic 1 + Epic 5 |
| FR-18 | Epic 1 |
| FR-12 | Epic 5 |
| ~~FR-13~~ | — (retiré) |
| FR-14 | Epic 6 |
| FR-15 | Epic 6 |
| FR-16 | Epic 6 |
| FR-17 | Epic 7 |

---

## Epic List

### Epic 1: Fondation app & accueil intervention
L'utilisateur ouvre **NOTES DU SECOURISTE**, voit un accueil offline-ready et démarre une **nouvelle intervention** en un geste.  
**FRs :** FR-1, FR-2 · **UX :** UX-DR1, UX-DR2, UX-DR3, UX-DR11

### Epic 2: Saisie notes secouriste (blocs terrain)
L'utilisateur remplit les **notes secouriste** par blocs avec sauvegarde auto et ergonomie terrain (hors bloc constantes vitales).  
**FRs :** FR-3, FR-4, FR-5 · **UX :** UX-DR7

### Epic 3: Relevés vitaux, alertes & références
L'utilisateur enregistre des **relevés horodatés** (TA sys/dia, FC…), voit les **alertes**, consulte les **ⓘ** et configure les **plages de référence**.  
**FRs :** FR-6, FR-7, FR-8 · **UX :** UX-DR4, UX-DR5, UX-DR6

### Epic 4: Transmission orale
L'utilisateur lit un **récap** et un **script de transmission** offline pour dicter au chef d'équipe / SAMU.  
**FRs :** FR-9, FR-10 · **UX :** UX-DR9

### Epic 5: Historique & cycle de vie
L'utilisateur retrouve ses interventions et **clôture** en lecture seule.  
**FRs :** FR-11, FR-12 · **UX :** UX-DR10

### Epic 6: Aide mémoire repères
L'utilisateur consulte et **personnalise** des fiches repères Markdown offline.  
**FRs :** FR-14, FR-15, FR-16 · **UX :** UX-DR8

**Décision (2026-05-27) :** pas de viewer PDF interne. Les PDF, si présents, sont ouverts via le viewer PDF du téléphone (app externe).

### Epic 7: Confidentialité & suppression
L'utilisateur **supprime** manuellement des interventions (pas de purge automatique).  
**FRs :** FR-17

---

## Epic 1: Fondation app & accueil intervention

Permettre l'installation du socle Android et le démarrage d'une intervention depuis l'accueil.

### Story 1.1: Initialiser le projet Android

As a **développeur**,
I want **un projet Kotlin Compose avec modules et Material 3 classique**,
So that **les epics suivantes partagent une base stable**.

**Acceptance Criteria:**

**Given** un dépôt vide  
**When** le projet est généré  
**Then** modules existent : `:app`, `:core-data`, `:core-ui`, `:feature-intervention_notes`, `:feature-aide_memoire`  
**And** dépendances : Compose BOM, `material3`, Room, Hilt, Navigation Compose  
**And** `MaterialTheme` appliqué (pas `MaterialExpressiveTheme`)  
**And** minSdk ≥ 26

**Implements:** NFR-7, NFR-8, UX-DR3

---

### Story 1.2: Thème Terrain nocturne & garde-fou terminologie

As a **secouriste**,
I want **une interface sombre lisible et sans le mot « bilan »**,
So that **je me repère vite de nuit et je ne confonds pas avec un document officiel**.

**Acceptance Criteria:**

**Given** l'app installée  
**When** je parcours les strings `strings.xml` (fr)  
**Then** aucune occurrence de « bilan » ou « fiche bilan »  
**And** `ColorScheme` sombre applique les tokens UX (primary #E53935, alert-warning, alert-critical, status-draft, status-closed)  
**And** typographie Material 3 standard (non expressive)

**Implements:** FR-2, UX-DR2

---

### Story 1.3: Écran d'accueil & navigation principale

As a **secouriste**,
I want **un accueil qui liste mes interventions et me permet d'en démarrer une nouvelle**,
So that **je reprends ou je crée sans chercher un menu Historique**.

**Acceptance Criteria:**

**Given** l'app lancée offline  
**When** j'arrive sur l'accueil (S-01)  
**Then** je vois le titre **NOTES DU SECOURISTE**  
**And** une **icône seule** Aide mémoire dans le header (`contentDescription` = « Aide mémoire »)  
**And** un **toggle thème** en haut à droite  
**And** le CTA **Nouvelle intervention** (sous-titre **Notes secouriste**) **au-dessus** de la liste  
**And** la **liste** des interventions en dessous (ligne 1 identité, ligne 2 date-heure, pastille statut) ou état vide avec CTA  
**And** **aucun** libellé « Hors ligne » ni écran Historique dédié  
**And** tap icône Aide mémoire → placeholder aide ; tap ligne → détail intervention

**Implements:** UX-DR1, FR-2, FR-11 (liste)

---

### Story 1.5: Thème clair/sombre & politique d'ouverture

As a **secouriste**,
I want **choisir un thème clair ou sombre et comment l'app s'ouvre**,
So that **l'app reste lisible de jour comme de nuit**.

**Acceptance Criteria:**

**Given** Réglages → Apparence  
**When** je choisis **Thème système** (défaut) ou **Dernier thème choisi**  
**Then** au cold start le thème appliqué respecte la politique (D-027, D-028)  
**When** je bascule le toggle sur l'accueil  
**Then** le thème change immédiatement et le dernier choix explicite est mémorisé  
**And** les deux thèmes passent la revue contraste des écrans critiques

**Implements:** FR-18, UX-DR13

---

### Story 1.4: Créer une nouvelle intervention

As a **secouriste**,
I want **démarrer une intervention en un tap**,
So that **je commence mes notes sans délai**.

**Acceptance Criteria:**

**Given** l'accueil  
**When** je tape **Nouvelle intervention**  
**Then** une `Intervention` est créée en statut **brouillon** avec horodatage &lt; 2 s  
**And** l'écran de saisie notes s'ouvre sans étape intermédiaire  
**And** cela fonctionne en mode avion

**Implements:** FR-1, NFR-3

---

## Epic 2: Saisie notes secouriste (blocs terrain)

Permettre la saisie structurée des blocs non-vitaux avec persistance automatique.

### Story 2.1: Schéma v1 & persistance des blocs notes

As a **secouriste**,
I want **mes blocs notes sauvegardés automatiquement**,
So that **je ne perds rien si je quitte l'app**.

**Acceptance Criteria:**

**Given** une intervention brouillon  
**When** je modifie un champ d'un bloc (Contexte, Victime, Gestes, Évolution, Transmission — hors constantes vitales)  
**Then** la valeur est persistée en Room en &lt; 500 ms sans bouton Enregistrer  
**And** `schemaVersion` = v1 est stocké sur l'intervention  
**And** champs minimum PRD présents : lieu/contexte, victime/âge, gestes, évolution, éléments transmission

**Implements:** FR-3, FR-4, NFR-1

---

### Story 2.2: UI blocs InterventionBlockCard

As a **secouriste**,
I want **remplir des cartes blocs scrollables**,
So that **je note comme sur un carnet par sections**.

**Acceptance Criteria:**

**Given** l'écran saisie (S-02)  
**When** je scroll les blocs  
**Then** chaque bloc est une `InterventionBlockCard` avec titre et champs adaptés  
**And** barre d'actions basse visible (placeholders Récap / Script / Clôturer)

**Implements:** FR-3, UX-DR7

---

### Story 2.3: Reprise après interruption

As a **secouriste**,
I want **retrouver ma position de saisie**,
So that **je reprends l'intervention sans chercher où j'en étais**.

**Acceptance Criteria:**

**Given** une saisie en cours à un bloc donné  
**When** je tue l'app et la relance  
**Then** les données des blocs sont intactes  
**And** l'écran rouvre sur la même intervention brouillon `[ASSUMPTION:]` même scroll/bloc

**Implements:** FR-4

---

### Story 2.4: Ergonomie terrain

As a **secouriste**,
I want **de gros boutons et un bon contraste**,
So that **je saisis avec des gants ou une main**.

**Acceptance Criteria:**

**Given** l'écran saisie  
**When** je mesure les cibles tactiles principales  
**Then** zones ≥ 48 dp  
**And** contraste texte/fond ≥ WCAG AA sur champs et boutons primaires  
**And** thème sombre actif par défaut

**Implements:** FR-5, NFR-6

---

## Epic 3: Relevés vitaux, alertes & références

Permettre relevés multiples horodatés, alertes et réglages de référence.

### Story 3.1: Entité VitalReading & liste par constante

As a **secouriste**,
I want **ajouter plusieurs relevés pour une même constante**,
So that **je suis l'évolution (ex. FC) pendant l'intervention**.

**Acceptance Criteria:**

**Given** le bloc Constantes sur une intervention brouillon  
**When** j'ajoute 2 relevés FC avec valeurs différentes  
**Then** chaque relevé a un `recordedAt` (date+heure) en base  
**And** l'UI liste affiche **HH:mm** par relevé (dernier en haut)  
**And** je peux supprimer un relevé (confirmation si seul relevé de la constante)

**Implements:** FR-6 (partiel)

---

### Story 3.2: Saisie TA systolique puis diastolique

As a **secouriste**,
I want **saisir TA en deux champs consécutifs sur une ligne**,
So that **c'est naturel comme sur le terrain**.

**Acceptance Criteria:**

**Given** un nouveau relevé TA  
**When** je saisis systolique puis diastolique  
**Then** un seul `recordedAt` est partagé  
**And** l'affichage liste montre `120 / 80`  
**And** le relevé est incomplet si une seule des deux valeurs est remplie (indicateur visuel discret)

**Implements:** FR-6, UX-DR5

---

### Story 3.3: Antidatage date & heure d'un relevé

As a **secouriste**,
I want **modifier la date et l'heure d'un relevé**,
So that **je corrige une mesure saisie en retard**.

**Acceptance Criteria:**

**Given** un relevé existant  
**When** je tape l'horodatage affiché  
**Then** s'ouvre DateTimeEditSheet (date + heure + minutes)  
**And** après Enregistrer, `recordedAt` est mis à jour en base  
**And** si la date ≠ jour de début d'intervention, l'UI affiche `dd/MM HH:mm`

**Implements:** FR-6, UX-DR6

---

### Story 3.4: Alertes couleur non bloquantes

As a **secouriste**,
I want **voir si un relevé est hors norme sans être bloqué**,
So that **je continue l'intervention**.

**Acceptance Criteria:**

**Given** des plages de référence configurées  
**When** je saisis un relevé hors plage limite ou critique  
**Then** le champ/ligne affiche ambre ou rouge en &lt; 500 ms  
**And** aucun modal ne bloque la navigation ou l'ajout d'un autre relevé  
**And** chaque relevé peut avoir un niveau d'alerte distinct

**Implements:** FR-7

---

### Story 3.5: ⓘ plages de référence par âge

As a **secouriste**,
I want **voir les valeurs normales pour l'âge de la victime**,
So that **je interprète ma mesure**.

**Acceptance Criteria:**

**Given** un bloc Constantes avec ⓘ sur un type de mesure  
**When** je tape ⓘ  
**Then** une bottom sheet affiche les plages de la tranche d'âge active `[ASSUMPTION:]` liée à l'âge victime ou tranche manuelle

**Implements:** FR-8 (consultation)

---

### Story 3.6: Réglages valeurs de référence

As a **secouriste**,
I want **personnaliser les plages normales et alertes**,
So that **l'app correspond à ma formation / pratique**.

**Acceptance Criteria:**

**Given** Réglages → Valeurs de référence (S-10)  
**When** je modifie min/max pour une constante et une tranche d'âge  
**Then** la prochaine saisie utilise les nouvelles plages  
**And** **Réinitialiser** restaure les défauts usine

**Implements:** FR-8

---

## Epic 4: Transmission orale

Permettre récap et script offline pour dictée.

### Story 4.1: Écran récap transmission

As a **secouriste**,
I want **un récap lisible de toute l'intervention**,
So that **mon chef d'équipe lit l'écran ou je prépare ma dictée**.

**Acceptance Criteria:**

**Given** une intervention brouillon ou clôturée avec données  
**When** j'ouvre **Récap** (S-03)  
**Then** le contenu suit l'ordre : contexte → victime → constantes (relevés HH:mm, TA 120/80) → gestes → évolution  
**And** typo ≥ 22 sp, lisible `[ASSUMPTION:]` à ~1 m  
**And** aucun export fichier

**Implements:** FR-9, UX-DR9

---

### Story 4.2: Script de transmission rule-based

As a **secouriste**,
I want **un texte court généré offline pour ma transmission orale**,
So that **je ne cherche pas mes mots sous stress**.

**Acceptance Criteria:**

**Given** une intervention avec champs remplis  
**When** j'ouvre **Script de transmission** (S-04) en mode avion  
**Then** le texte est généré en &lt; 3 s sans réseau  
**And** format inclut horodatages relevés et alertes mentionnées  
**And** footer **Relire avant transmission — ne remplace pas votre jugement**  
**And** aucun appel LLM cloud

**Implements:** FR-10, NFR-3

---

## Epic 5: Historique & cycle de vie

Gérer **affichage liste sur accueil** (pas d'écran S-05) et clôture lecture seule.

### Story 5.1: Liste interventions sur accueil (enrichissement)

As a **secouriste**,
I want **voir NOM Prénom - Âge et la date sur chaque ligne de l'accueil**,
So that **je retrouve la bonne victime d'un coup d'œil**.

**Acceptance Criteria:**

**Given** plusieurs interventions en base (nom/prénom/âge renseignés ou non)  
**When** je suis sur l'accueil (S-01)  
**Then** la liste est triée par date décroissante  
**And** chaque ligne affiche L1 `NOM Prénom - Âge` (ou placeholder) et L2 `date - heure` et pastille **Brouillon** / **Clôturée**  
**And** tap ouvre le détail (S-06)  
**And** pas de route navigation « Historique » séparée

**Implements:** FR-11, UX-DR10

---

### Story 5.2: Clôturer en lecture seule

As a **secouriste**,
I want **clôturer une intervention**,
So that **elle ne soit plus modifiable par erreur**.

**Acceptance Criteria:**

**Given** une intervention brouillon  
**When** je confirme **Clôturer**  
**Then** le statut passe à **clôturée**  
**And** tous champs et relevés sont non éditables  
**And** bannière « Intervention clôturée — lecture seule »

**Implements:** FR-12

---

## Epic 6: Aide mémoire repères

Consulter et personnaliser fiches Markdown offline.

### Story 6.1: Pack par défaut embarqué

As a **secouriste**,
I want **des fiches repères dès la première ouverture**,
So that **l'aide mémoire est utile sans configuration**.

**Acceptance Criteria:**

**Given** première installation, mode avion  
**When** j'ouvre Aide mémoire  
**Then** 5–10 fiches PSE sont visibles (assets locaux)  
**And** chaque fiche a titre situation + repères Markdown + image optionnelle

**Implements:** FR-15

---

### Story 6.2: Navigation situation → repères

As a **secouriste**,
I want **trouver un protocole en 2 taps**,
So that **je gagne du temps sur le terrain**.

**Acceptance Criteria:**

**Given** l'accueil  
**When** je vais à Aide mémoire → je choisis une situation  
**Then** le détail (S-08) affiche repères rendus (RepereMarkdownView) en &lt; 2 s  
**And** retour préserve l'intervention brouillon si ouverte

**Implements:** FR-14, UX-DR8, NFR-2

---

### Story 6.3: Éditeur aide mémoire

As a **secouriste**,
I want **modifier ou ajouter des fiches**,
So that **mon aide mémoire colle à mon équipe**.

**Acceptance Criteria:**

**Given** l'écran gestion aide mémoire (S-09)  
**When** j'édite une fiche via éditeur simplifié (pas syntaxe brute obligatoire)  
**Then** les changements persistent après redémarrage  
**And** avertissement si intervention brouillon ouverte `[ASSUMPTION:]`

**Implements:** FR-16

---

## Epic 7: Confidentialité & suppression

Suppression manuelle des données (pas de purge automatique — D-034).

### Story 7.1: Suppression manuelle (liste accueil)

As a **secouriste**,
I want **supprimer des interventions quand je le décide**,
So that **je maîtrise les données sensibles sur mon téléphone**.

**Acceptance Criteria:**

**Given** la liste interventions sur l'accueil (S-01)  
**When** je supprime via icône poubelle ou sélection multiple  
**Then** une confirmation est demandée  
**And** l'intervention et ses notes sont supprimées définitivement  
**And** aucune purge automatique par âge n'existe dans l'app

**Implements:** FR-17, NFR-4

---

## Validation finale (Step 4)

| Contrôle | Résultat |
|----------|----------|
| FR-1 à FR-17 couverts | ✅ |
| UX-DR1 à UX-DR12 couverts | ✅ |
| Pas de dépendance story future | ✅ |
| Tables créées au besoin (pas tout en 1.1) | ✅ Story 2.1, 3.1, 6.1 |
| Epics valeur utilisateur | ✅ |
| Architecture manquante | ⚠️ Epic 1.1 s'appuie sur addendum ; affiner après `bmad-create-architecture` |

**Prêt pour développement.** Prochaine étape recommandée : **architecture** puis **dev-story** / implémentation par epic.
